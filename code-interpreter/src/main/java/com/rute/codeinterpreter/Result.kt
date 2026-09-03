package com.rute.codeinterpreter

data class InterpreterResult(
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
    val timedOut: Boolean = false,
    val files: List<OutputFile> = emptyList()
)

data class OutputFile(
    val name: String,
    val path: String,
    val mimeType: String
)
