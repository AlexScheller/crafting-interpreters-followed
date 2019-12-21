package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

class Scanner {

	private final String source;
	private final TokenList tokens;

	private int start = 0;
	private int current = 0;
	private int line = 1;
	private int col = 1;

	Scanner(String source) {
		this.source = source;
		this.tokens = new TokenList();
	}

	public String stateAsString() {
		return String.format(
			"Start: %d, Current: %d, Line: %d, Col: %d",
			this.start, this.current, this.line, this.col
		);
	}

	public String tokensAsString() {
		return this.tokens.asString();
	}

	public void scanTokens() {
		while(!this.isAtEnd()) {
			this.start = this.current;
			this.scanToken();
		}
		this.tokens.addToken(new Token(EOF, "", null, this.line, this.col));
	}

	public void scanToken() {
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
			case '!': this.addToken(this.nextMatches('=') ? BANG_EQUAL : BANG); break;
			case '=': this.addToken(this.nextMatches('=') ? BANG_EQUAL : EQUAL); break;
			case '<': this.addToken(this.nextMatches('=') ? LESS_EQUAL : LESS); break;
			case '>': this.addToken(this.nextMatches('=') ? GREATER_EQUAL : GREATER); break;
			case '/':
				if (this.nextMatches('/')) {
					while (this.peek() != '\n' && !this.isAtEnd()) this.advanceCurrent();
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
				Lox.error(line, col, "Unexpected character '" + c + "'.");
				break;
		}
	}

	private void handleString() {
		while (this.peek() != '"' && !this.isAtEnd()) {
			if (this.peek() == '\n') {
				this.line++;
				this.col = 1;
			}
			this.advanceCurrent();
		}
		if (this.isAtEnd()) {
			Lox.error(this.line, this.col, "Unterminated string.");
			return;
		}
		this.advanceCurrent(); // closing '"'
		// trim quotes
		String value = this.source.substring(this.start + 1, this.current - 1);
		this.addToken(STRING, value);
	}

	private boolean nextMatches(char expected) {
		if (isAtEnd()) return false;
		if (this.source.charAt(this.current) != expected) return false;
		this.current++;
		return true;
	}

	private char peek() {
		if (this.isAtEnd()) return '\0';
		return this.source.charAt(this.current);
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
		String text = this.source.substring(this.start, this.current);
		this.tokens.addToken(
			new Token(type, text, literal, this.line, this.col)
		);
	}

}