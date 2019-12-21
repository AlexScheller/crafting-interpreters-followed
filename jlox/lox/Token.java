package lox;

class Token {

	final TokenType type;
	final String lexeme;
	final Object literal;
	final int line;
	final int col;

	Token(TokenType type, String lexeme, Object literal, int line, int col) {
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literal;
		this.line = line;
		// note that the column is of the first character of the token.
		this.col = col;
	}

	public String asString() {
		return "<" + type + " " + lexeme + " " + literal + ">";
	}

}