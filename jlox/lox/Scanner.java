package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

class Scanner {

	private final SourceCode source;
	private final TokenList tokens;

	private int tokenStart = 0;
	private int current = 0;
	private int line = 1;
	private int col = 1;

	private static final Map<String, TokenType> keywords;

	static {
		keywords = new HashMap<>();
		keywords.put("class", CLASS);
		keywords.put("this", THIS);
		keywords.put("super", SUPER);
		keywords.put("var", VAR);
		keywords.put("fun", FUN);
		keywords.put("nil", NIL);
		keywords.put("print", PRINT);
		keywords.put("return", RETURN);
		keywords.put("and", AND);
		keywords.put("or", OR);
		keywords.put("if", IF);
		keywords.put("else", ELSE);
		keywords.put("for", FOR);
		keywords.put("while", WHILE);
		keywords.put("false", FALSE);
		keywords.put("true", TRUE);
	}

	Scanner(String source) {
		this.source = new SourceCode(source);
		this.tokens = new TokenList();
	}

	public String stateAsString() {
		return String.format(
			"Start: %d, Current: %d, Line: %d, Col: %d",
			this.tokenStart, this.current, this.line, this.col
		);
	}

	public String tokensAsString() {
		return this.tokens.asString();
	}

	public void scanTokens() {
		while(!this.isAtEnd()) {
			this.tokenStart = this.current;
			this.scanNextToken();
		}
		this.tokens.addToken(new Token(EOF, "", null, this.line, this.col));
	}

	public void scanNextToken() {
		char c = this.advanceCurrent();
		switch (c) {
			case '(': this.addToken(LEFT_PAREN); break;
			case ')': this.addToken(RIGHT_PAREN); break;
			case '{': this.addToken(LEFT_BRACE); break;
			case '}': this.addToken(RIGHT_BRACE); break;
			case ',': this.addToken(COMMA); break;
			case '.': this.addToken(DOT); break;
			case '-': this.addToken(MINUS); break;
			case '+': this.addToken(PLUS); break;
			case ';': this.addToken(SEMICOLON); break;
			case '*': this.addToken(STAR); break;
			case '!': this.addToken(this.advanceCurrentIfNextMatches('=') ? BANG_EQUAL : BANG); break;
			case '=': this.addToken(this.advanceCurrentIfNextMatches('=') ? BANG_EQUAL : EQUAL); break;
			case '<': this.addToken(this.advanceCurrentIfNextMatches('=') ? LESS_EQUAL : LESS); break;
			case '>': this.addToken(this.advanceCurrentIfNextMatches('=') ? GREATER_EQUAL : GREATER); break;
			case '/':
				if (this.advanceCurrentIfNextMatches('/')) {
					while (this.peek() != '\n' && !this.isAtEnd()) this.advanceCurrent();
				} else if (this.advanceCurrentIfNextMatches('*')) { // begin comment block
					boolean commentFinished = false;
					int openLine = this.line;
					int openCol = this.col;
					for (char curr = this.advanceCurrent(); !this.isAtEnd(); curr = this.advanceCurrent()) {
						// System.out.println("Parsing comment, curr char: " + curr);
						// System.out.println(this.stateAsString());
						if (curr == '\n') {
							this.line++;
							this.col = 0;
						} else if (curr == '*' && this.advanceCurrentIfNextMatches('/')) {
							commentFinished = true;
							break;
						}
					}
					if (!commentFinished) {
						Lox.error(
							openLine, openCol,
							"Reached end of file while scanning open comment block.",
							this.source.getLine(openLine - 1),
							"scanning"
						);
					}
				} else {
					this.addToken(SLASH);
				}
				break;
			case '"': this.handleString(); break;
			case ' ':
			case '\r':
			case '\t':
				break;
			case '\n':
				this.line++;
				this.col = 0;
				break;
			default:
				if (this.isDigit(c)) {
					this.handleNumber();
				} else if (this.isAlpha(c)) {
					this.handleIdentifier();
				} else {
					Lox.error(
						this.line, this.col,
						"Unexpected character '" + c + "'.",
						this.source.getLine(this.line - 1),
						"scanning"
					);
				}
				break;
		}
	}

	private void handleIdentifier() {
		while (this.isAlphaNumberic(this.peek())) this.advanceCurrent();
		String text = source.substring(this.tokenStart, this.current);
		TokenType type = keywords.get(text);
		if (type == null) type = IDENTIFIER;
		this.addToken(type);
	}

	private void handleNumber() {
		while (this.isDigit(this.peek())) this.advanceCurrent();
		// fractional part
		if (this.peek() == '.' && this.isDigit(this.peekNext())) {
			// consume the '.'
			this.advanceCurrent();
			while (this.isDigit(this.peek())) this.advanceCurrent();
		}
		this.addToken(
			NUMBER,
			Double.parseDouble(
				this.source.substring(this.tokenStart, this.current)
			)
		);
	}

	private void handleString() {
		int beginLine = this.line;
		int beginCol = this.col;
		while (this.peek() != '"' && !this.isAtEnd()) {
			if (this.peek() == '\n') {
				this.line++;
				this.col = 0;
			}
			this.advanceCurrent();
		}
		if (this.isAtEnd()) {
			Lox.error(
				beginLine, beginCol,
				"Unterminated string.",
				this.source.getLine(beginLine - 1),
				"scanning"
			);
			return;
		}
		this.advanceCurrent(); // closing '"'
		// trim quotes
		String value = this.source.substring(this.tokenStart + 1, this.current - 1);
		this.addToken(STRING, value);
	}

	private boolean advanceCurrentIfNextMatches(char expected) {
		if (isAtEnd()) return false;
		if (this.source.charAt(this.current) != expected) return false;
		this.current++;
		this.col++;
		return true;
	}

	private char peek() {
		if (this.isAtEnd()) return '\0';
		return this.source.charAt(this.current);
	}

	private char peekNext() {
		if (this.current + 1 >= this.source.length()) return '\0';
		return source.charAt(this.current + 1);
	}

	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') ||
			   (c >= 'A' && c <= 'Z') ||
			   c == '_';
	}

	private boolean isAlphaNumberic(char c) {
		return this.isAlpha(c) || this.isDigit(c);
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isAtEnd() {
		return current >= this.source.length();
	}

	private char advanceCurrent() {
		this.current++;
		this.col++;
		return this.source.charAt(this.current - 1);
	}

	private void addToken(TokenType type) {
		this.addToken(type, null);
	}

	private void addToken(TokenType type, Object literal) {
		String text = this.source.substring(this.tokenStart, this.current);
		this.tokens.addToken(
			new Token(type, text, literal, this.line, this.col)
		);
	}

}