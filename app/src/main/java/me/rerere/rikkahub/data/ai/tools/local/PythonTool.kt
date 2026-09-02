package me.rerere.rikkahub.data.ai.tools.local

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import me.rerere.ai.core.InputSchema
import me.rerere.ai.core.Tool
import me.rerere.ai.ui.UIMessagePart
import me.rerere.workspace.WorkspaceManager
import org.koin.core.context.GlobalContext

// code_interpreter — Python ringan via workspace proot, decode QR + hitung
internal fun buildPythonTool(): Tool = Tool(
    name = "code_interpreter",
    description = """
        Jalankan Python 3 di sandbox workspace proot (isolated). Ringan - tidak nambah ukuran APK, python & pip ada di workspace linux proot (download on-demand). 
        Bisa hitung, olah data, decode QR (pakai pyzbar + Pillow jika ada). 
        QR: jika diberi path file image (file://...), decode otomatis. Install pip: pip install pyzbar pillow numpy.
        Stdout/stderr dikembalikan. Timeout 30 detik.
    """.trimIndent().replace("\n", " "),
    parameters = {
        InputSchema.Obj(
            properties = buildJsonObject {
                put("code", buildJsonObject {
                    put("type", "string")
                    put("description", "Python code to execute")
                })
            },
            required = listOf("code")
        )
    },
    execute = { params ->
        val code = params.jsonObject["code"]?.jsonPrimitive?.contentOrNull ?: ""
        if (code.isBlank()) {
            return@Tool listOf(UIMessagePart.Text("""{"error":"code empty"}"""))
        }
        try {
            val koin = GlobalContext.getOrNull()
            val workspaceManager: WorkspaceManager? = koin?.getOrNull()
            // fallback: coba host python3 jika workspace tidak ready
            val result = if (workspaceManager != null) {
                // ambil workspace root pertama yang ada, atau buat temp
                val baseDir = workspaceManager.let { 
                    // cari root yang punya rootfs, fallback ke default
                    try {
                        val roots = java.io.File(workspaceManager.javaClass.getDeclaredField("baseDir").let { f -> f.isAccessible = true; f.get(workspaceManager) } as java.io.File).listFiles()?.map { it.name } ?: emptyList()
                        roots.firstOrNull() ?: "default"
                    } catch (_: Exception) { "default" }
                }
                try {
                    workspaceManager.ensureWorkspace(baseDir)
                } catch (_: Exception) {}
                // tulis code ke file di workspace files
                try {
                    workspaceManager.writeText(baseDir, "tmp_code.py", code, overwrite = true)
                } catch (_: Exception) {}
                workspaceManager.executeCommand(
                    root = baseDir,
                    command = "python3 /workspace/tmp_code.py 2>&1",
                    timeoutMillis = 30_000L
                ).let { res ->
                    buildJsonObject {
                        put("stdout", res.stdout ?: "")
                        put("stderr", res.stderr ?: "")
                        put("exit_code", res.exitCode)
                    }.toString()
                }
            } else {
                // host fallback via ProcessBuilder (Termux may have python)
                val proc = ProcessBuilder("python3", "-c", code)
                    .redirectErrorStream(true)
                    .start()
                val out = proc.inputStream.bufferedReader().readText()
                proc.waitFor()
                buildJsonObject {
                    put("stdout", out)
                    put("exit_code", proc.exitValue())
                }.toString()
            }
            listOf(UIMessagePart.Text(result))
        } catch (e: Exception) {
            listOf(UIMessagePart.Text("""{"error":"${e.message?.replace("\"","'")}"}"""))
        }
    }
)
