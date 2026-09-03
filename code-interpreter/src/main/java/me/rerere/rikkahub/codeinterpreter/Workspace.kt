package me.rerere.rikkahub.codeinterpreter

import android.content.Context
import java.io.File

class Workspace(context: Context) {
    val root: File = File(context.filesDir, "code_interpreter")
    val input: File = File(root, "input")
    val output: File = File(root, "output")
    val temp: File = File(root, "temp")

    init {
        input.mkdirs()
        output.mkdirs()
        temp.mkdirs()
    }

    fun safeInput(name: String): File = safeChild(input, name)
    fun safeOutput(name: String): File = safeChild(output, name)

    fun listOutputs(): List<File> = output.listFiles()?.toList() ?: emptyList()

    fun clearTemp() {
        temp.listFiles()?.forEach { it.deleteRecursively() }
        temp.mkdirs()
    }

    private fun safeChild(base: File, name: String): File {
        require(!name.contains("..")) { "Invalid path" }
        val file = File(base, name).canonicalFile
        require(file.path.startsWith(base.canonicalFile.path + File.separator)) {
            "Path escapes workspace"
        }
        return file
    }
}
