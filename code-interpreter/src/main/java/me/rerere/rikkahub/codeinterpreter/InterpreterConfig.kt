package me.rerere.rikkahub.codeinterpreter

data class InterpreterConfig(
    val timeoutMs: Long = 5_000L,
    val maxOutputChars: Int = 64 * 1024,
    val maxLoopIterations: Int = 10_000
)
