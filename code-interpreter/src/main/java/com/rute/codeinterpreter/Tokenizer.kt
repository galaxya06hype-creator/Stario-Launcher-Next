package com.rute.codeinterpreter

internal enum class TokenType {
    NUMBER, STRING, IDENT,
    PLUS, MINUS, STAR, SLASH, PERCENT,
    EQ, EQEQ, NEQ, LT, LTE, GT, GTE,
    LPAREN, RPAREN, LBRACKET, RBRACKET, COMMA, COLON,
    NEWLINE, EOF,
    KEYWORD
}

internal data class Token(val type: TokenType, val text: String, val line: Int)

internal class Tokenizer(private val source: String) {
    private val tokens = mutableListOf<Token>()
    private var i = 0
    private var line = 1

    fun tokenize(): List<Token> {
        while (i < source.length) {
            val c = source[i]
            when {
                c == ' ' || c == '\t' || c == '\r' -> i++
                c == '\n' -> { tokens += Token(TokenType.NEWLINE, "\n", line); line++; i++ }
                c == '#' -> skipComment()
                c.isDigit() -> readNumber()
                c == '"' || c == '\'' -> readString(c)
                c.isLetter() || c == '_' -> readIdent()
                else -> readSymbol()
            }
        }
        tokens += Token(TokenType.NEWLINE, "\n", line)
        tokens += Token(TokenType.EOF, "", line)
        return tokens
    }

    private fun skipComment() {
        while (i < source.length && source[i] != '\n') i++
    }

    private fun readNumber() {
        val start = i
        var dot = false
        while (i < source.length) {
            val c = source[i]
            if (c.isDigit()) i++
            else if (c == '.' && !dot) { dot = true; i++ }
            else break
        }
        tokens += Token(TokenType.NUMBER, source.substring(start, i), line)
    }

    private fun readString(quote: Char) {
        i++
        val sb = StringBuilder()
        while (i < source.length) {
            val c = source[i++]
            if (c == quote) {
                tokens += Token(TokenType.STRING, sb.toString(), line)
                return
            }
            if (c == '\\' && i < source.length) {
                val e = source[i++]
                sb.append(
                    when (e) {
                        'n' -> '\n'
                        'r' -> '\r'
                        't' -> '\t'
                        '\\' -> '\\'
                        '"' -> '"'
                        '\'' -> '\''
                        else -> e
                    }
                )
            } else {
                sb.append(c)
            }
        }
        error("Unterminated string on line $line")
    }

    private fun readIdent() {
        val start = i
        while (i < source.length && (source[i].isLetterOrDigit() || source[i] == '_')) i++
        val word = source.substring(start, i)
        val keywords = setOf("if", "else", "for", "in", "range", "True", "False", "None", "and", "or", "not")
        tokens += Token(if (word in keywords) TokenType.KEYWORD else TokenType.IDENT, word, line)
    }

    private fun readSymbol() {
        val c = source[i]
        val next = if (i + 1 < source.length) source[i + 1] else '\u0000'
        val two = "$c$next"
        when (two) {
            "==" -> { tokens += Token(TokenType.EQEQ, two, line); i += 2 }
            "!=" -> { tokens += Token(TokenType.NEQ, two, line); i += 2 }
            "<=" -> { tokens += Token(TokenType.LTE, two, line); i += 2 }
            ">=" -> { tokens += Token(TokenType.GTE, two, line); i += 2 }
            else -> {
                val t = when (c) {
                    '+' -> TokenType.PLUS
                    '-' -> TokenType.MINUS
                    '*' -> TokenType.STAR
                    '/' -> TokenType.SLASH
                    '%' -> TokenType.PERCENT
                    '=' -> TokenType.EQ
                    '<' -> TokenType.LT
                    '>' -> TokenType.GT
                    '(' -> TokenType.LPAREN
                    ')' -> TokenType.RPAREN
                    '[' -> TokenType.LBRACKET
                    ']' -> TokenType.RBRACKET
                    ',' -> TokenType.COMMA
                    ':' -> TokenType.COLON
                    else -> error("Unexpected '$c' on line $line")
                }
                tokens += Token(t, c.toString(), line)
                i++
            }
        }
    }
}
