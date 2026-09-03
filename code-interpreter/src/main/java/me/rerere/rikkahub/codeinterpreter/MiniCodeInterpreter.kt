package me.rerere.rikkahub.codeinterpreter

import android.content.Context
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class MiniCodeInterpreter(
    context: Context,
    private val config: InterpreterConfig = InterpreterConfig()
) {
    private val workspace = Workspace(context.applicationContext)

    fun execute(code: String): InterpreterResult {
        val out = StringBuilder()
        val executor = Executors.newSingleThreadExecutor()

        val future = executor.submit<InterpreterResult> {
            try {
                val tokens = Tokenizer(code).tokenize()
                val program = Parser(tokens).parseProgram()
                Evaluator(config, workspace, out).run(program)
                InterpreterResult(
                    stdout = out.toString(),
                    stderr = "",
                    exitCode = 0,
                    timedOut = false,
                    files = emptyList()
                )
            } catch (t: Throwable) {
                InterpreterResult(
                    stdout = out.toString(),
                    stderr = "${t::class.simpleName}: ${t.message}",
                    exitCode = 1,
                    timedOut = false,
                    files = emptyList()
                )
            }
        }

        return try {
            future.get(config.timeoutMs, TimeUnit.MILLISECONDS)
        } catch (_: TimeoutException) {
            future.cancel(true)
            InterpreterResult(
                stdout = out.toString(),
                stderr = "Execution timed out",
                exitCode = 124,
                timedOut = true
            )
        } catch (t: Throwable) {
            InterpreterResult(
                stdout = out.toString(),
                stderr = "${t::class.simpleName}: ${t.message}",
                exitCode = 1
            )
        } finally {
            executor.shutdownNow()
        }
    }

    fun workspace(): Workspace = workspace
}
