package com.rute.codeinterpreter

import kotlin.math.abs

internal class Evaluator(
    private val config: InterpreterConfig,
    private val workspace: Workspace,
    private val stdout: StringBuilder
) {
    private val vars = mutableMapOf<String, Any?>()
    private var iterations = 0

    fun run(program: List<Stmt>) {
        program.forEach { exec(it) }
    }

    private fun exec(stmt: Stmt) {
        when (stmt) {
            is Stmt.ExprStmt -> eval(stmt.expr)
            is Stmt.Assign -> vars[stmt.name] = eval(stmt.expr)
            is Stmt.If -> {
                val cond = truthy(eval(stmt.condition))
                (if (cond) stmt.yes else stmt.no).forEach { exec(it) }
            }
            is Stmt.For -> {
                val value = eval(stmt.iterable)
                val list = when (value) {
                    is List<*> -> value
                    is IntRange -> value.toList()
                    is LongRange -> value.toList()
                    else -> error("Object is not iterable")
                }
                for (item in list) {
                    iterations++
                    if (iterations > config.maxLoopIterations) error("Loop iteration limit exceeded")
                    vars[stmt.name] = item
                    stmt.body.forEach { exec(it) }
                }
            }
        }
    }

    private fun eval(e: Expr): Any? = when (e) {
        is Expr.Num -> if (e.value % 1.0 == 0.0) e.value.toLong() else e.value
        is Expr.Str -> e.value
        is Expr.Bool -> e.value
        Expr.Null -> null
        is Expr.Var -> vars[e.name] ?: builtins()[e.name] ?: error("Name '${e.name}' is not defined")
        is Expr.Unary -> {
            val v = eval(e.value)
            when (e.op) {
                "-" -> -number(v)
                "not" -> !truthy(v)
                else -> error("Unknown unary operator")
            }
        }
        is Expr.Binary -> binary(eval(e.left), e.op, eval(e.right))
        is Expr.Call -> call(e.name, e.args.map { eval(it) })
        is Expr.ListExpr -> e.values.map { eval(it) }
    }

    private fun binary(a: Any?, op: String, b: Any?): Any? {
        return when (op) {
            "+" -> when {
                a is String || b is String -> stringify(a) + stringify(b)
                a is List<*> && b is List<*> -> a + b
                else -> number(a) + number(b)
            }
            "-" -> number(a) - number(b)
            "*" -> number(a) * number(b)
            "/" -> number(a) / number(b)
            "%" -> number(a) % number(b)
            "==" -> a == b
            "!=" -> a != b
            "<" -> number(a) < number(b)
            "<=" -> number(a) <= number(b)
            ">" -> number(a) > number(b)
            ">=" -> number(a) >= number(b)
            "and" -> truthy(a) && truthy(b)
            "or" -> truthy(a) || truthy(b)
            else -> error("Unknown operator: $op")
        }
    }

    private fun call(name: String, args: List<Any?>): Any? {
        return when (name) {
            "print" -> {
                stdout.append(args.joinToString(" ") { stringify(it) }).append('\n')
                trimOutput()
                null
            }
            "len" -> when (val v = args.singleOrNull() ?: error("len() needs 1 argument")) {
                is String -> v.length
                is List<*> -> v.size
                else -> error("len() unsupported for ${v?.javaClass?.simpleName}")
            }
            "sum" -> (args.single() as List<*>).sumOf { number(it) }
            "min" -> (args.single() as List<*>).minOf { number(it) }
            "max" -> (args.single() as List<*>).maxOf { number(it) }
            "abs" -> abs(number(args.single()))
            "int" -> number(args.single()).toLong()
            "float" -> number(args.single())
            "str" -> stringify(args.single())
            "range" -> {
                when (args.size) {
                    1 -> (0 until number(args[0]).toInt()).toList()
                    2 -> (number(args[0]).toInt() until number(args[1]).toInt()).toList()
                    else -> error("range() supports 1 or 2 args in Alpha V1")
                }
            }
            else -> error("Unknown function '$name'")
        }
    }

    private fun builtins(): Map<String, Any?> = mapOf()

    private fun number(v: Any?): Double = when (v) {
        is Number -> v.toDouble()
        else -> error("Expected number, got ${v?.javaClass?.simpleName}")
    }

    private fun truthy(v: Any?): Boolean = when (v) {
        null -> false
        is Boolean -> v
        is Number -> v.toDouble() != 0.0
        is String -> v.isNotEmpty()
        is Collection<*> -> v.isNotEmpty()
        else -> true
    }

    private fun stringify(v: Any?): String = when (v) {
        null -> "None"
        is Double -> if (v % 1.0 == 0.0) v.toLong().toString() else v.toString()
        else -> v.toString()
    }

    private fun trimOutput() {
        if (stdout.length > config.maxOutputChars) {
            stdout.setLength(config.maxOutputChars)
            stdout.append("\n[output truncated]")
        }
    }
}
