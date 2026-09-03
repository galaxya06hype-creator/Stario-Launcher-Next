package com.rute.codeinterpreter

internal class Parser(private val tokens: List<Token>) {
    private var p = 0

    fun parseProgram(): List<Stmt> {
        val out = mutableListOf<Stmt>()
        while (!check(TokenType.EOF)) {
            if (match(TokenType.NEWLINE)) continue
            out += statement()
        }
        return out
    }

    private fun statement(): Stmt {
        if (checkKeyword("if")) return parseIf()
        if (checkKeyword("for")) return parseFor()

        if (check(TokenType.IDENT) && peek(1).type == TokenType.EQ) {
            val name = advance().text
            advance()
            val expr = expression()
            endLine()
            return Stmt.Assign(name, expr)
        }

        val expr = expression()
        endLine()
        return Stmt.ExprStmt(expr)
    }

    private fun parseIf(): Stmt {
        expectKeyword("if")
        val condition = expression()
        expect(TokenType.COLON)
        endLine()
        val yes = parseIndentedBlock()
        var no = emptyList<Stmt>()
        if (checkKeyword("else")) {
            advance()
            expect(TokenType.COLON)
            endLine()
            no = parseIndentedBlock()
        }
        return Stmt.If(condition, yes, no)
    }

    private fun parseFor(): Stmt {
        expectKeyword("for")
        val name = expect(TokenType.IDENT).text
        expectKeyword("in")
        val iterable = expression()
        expect(TokenType.COLON)
        endLine()
        val body = parseIndentedBlock()
        return Stmt.For(name, iterable, body)
    }

    private fun parseIndentedBlock(): List<Stmt> {
        // Alpha V1 uses a simple colon + indentation syntax based on leading spaces.
        // Because the tokenizer drops indentation, require the body immediately after ':'.
        // A practical Android integration can preprocess code with a fixed 4-space block.
        val out = mutableListOf<Stmt>()
        while (!check(TokenType.EOF) && !checkKeyword("else")) {
            if (match(TokenType.NEWLINE)) continue
            out += statement()
        }
        return out
    }

    private fun expression(): Expr = logicalOr()

    private fun logicalOr(): Expr {
        var e = logicalAnd()
        while (checkKeyword("or")) { advance(); e = Expr.Binary(e, "or", logicalAnd()) }
        return e
    }

    private fun logicalAnd(): Expr {
        var e = equality()
        while (checkKeyword("and")) { advance(); e = Expr.Binary(e, "and", equality()) }
        return e
    }

    private fun equality(): Expr {
        var e = comparison()
        while (match(TokenType.EQEQ, TokenType.NEQ)) {
            val op = previous().text
            e = Expr.Binary(e, op, comparison())
        }
        return e
    }

    private fun comparison(): Expr {
        var e = term()
        while (match(TokenType.LT, TokenType.LTE, TokenType.GT, TokenType.GTE)) {
            val op = previous().text
            e = Expr.Binary(e, op, term())
        }
        return e
    }

    private fun term(): Expr {
        var e = factor()
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            val op = previous().text
            e = Expr.Binary(e, op, factor())
        }
        return e
    }

    private fun factor(): Expr {
        var e = unary()
        while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            val op = previous().text
            e = Expr.Binary(e, op, unary())
        }
        return e
    }

    private fun unary(): Expr {
        if (match(TokenType.MINUS)) return Expr.Unary("-", unary())
        if (checkKeyword("not")) { advance(); return Expr.Unary("not", unary()) }
        return primary()
    }

    private fun primary(): Expr {
        if (match(TokenType.NUMBER)) return Expr.Num(previous().text.toDouble())
        if (match(TokenType.STRING)) return Expr.Str(previous().text)
        if (checkKeyword("True")) { advance(); return Expr.Bool(true) }
        if (checkKeyword("False")) { advance(); return Expr.Bool(false) }
        if (checkKeyword("None")) { advance(); return Expr.Null }

        if (match(TokenType.LBRACKET)) {
            val values = mutableListOf<Expr>()
            if (!check(TokenType.RBRACKET)) {
                do { values += expression() } while (match(TokenType.COMMA))
            }
            expect(TokenType.RBRACKET)
            return Expr.ListExpr(values)
        }

        if (match(TokenType.IDENT) || (previous().type == TokenType.KEYWORD && previous().text !in setOf("if","else","for","in","range","and","or","not","True","False","None"))) {
            val name = previous().text
            if (match(TokenType.LPAREN)) {
                val args = mutableListOf<Expr>()
                if (!check(TokenType.RPAREN)) {
                    do { args += expression() } while (match(TokenType.COMMA))
                }
                expect(TokenType.RPAREN)
                return Expr.Call(name, args)
            }
            return Expr.Var(name)
        }

        if (match(TokenType.LPAREN)) {
            val e = expression()
            expect(TokenType.RPAREN)
            return e
        }

        error("Unexpected token '${peek().text}' on line ${peek().line}")
    }

    private fun endLine() {
        if (match(TokenType.NEWLINE)) return
        if (!check(TokenType.EOF)) error("Expected end of line at line ${peek().line}")
    }

    private fun expect(type: TokenType): Token {
        if (!check(type)) error("Expected $type at line ${peek().line}")
        return advance()
    }

    private fun expectKeyword(word: String) {
        if (!checkKeyword(word)) error("Expected '$word' at line ${peek().line}")
        advance()
    }

    private fun checkKeyword(word: String) = peek().type == TokenType.KEYWORD && peek().text == word
    private fun check(type: TokenType) = peek().type == type

    private fun match(vararg types: TokenType): Boolean {
        for (t in types) if (check(t)) { advance(); return true }
        return false
    }

    private fun peek(offset: Int = 0) = tokens[minOf(p + offset, tokens.lastIndex)]
    private fun previous() = tokens[p - 1]
    private fun advance(): Token = tokens[p++]

    private fun error(msg: String): Nothing = throw IllegalArgumentException(msg)
}
