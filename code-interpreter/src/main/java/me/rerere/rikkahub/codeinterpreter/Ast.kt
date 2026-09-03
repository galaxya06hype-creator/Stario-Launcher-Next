package me.rerere.rikkahub.codeinterpreter

internal sealed interface Stmt {
    data class ExprStmt(val expr: Expr) : Stmt
    data class Assign(val name: String, val expr: Expr) : Stmt
    data class If(val condition: Expr, val yes: List<Stmt>, val no: List<Stmt>) : Stmt
    data class For(val name: String, val iterable: Expr, val body: List<Stmt>) : Stmt
}

internal sealed interface Expr {
    data class Num(val value: Double) : Expr
    data class Str(val value: String) : Expr
    data class Bool(val value: Boolean) : Expr
    data object Null : Expr
    data class Var(val name: String) : Expr
    data class Unary(val op: String, val value: Expr) : Expr
    data class Binary(val left: Expr, val op: String, val right: Expr) : Expr
    data class Call(val name: String, val args: List<Expr>) : Expr
    data class ListExpr(val values: List<Expr>) : Expr
}
