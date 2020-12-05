using System;
using System.Collections.Generic;

namespace CSharpLox
{
	class Scanner
	{

		private readonly SourceCode source;
		private readonly TokenList tokens;
		private IErrorReporter errorReporter;

		private int tokenStart = 0;
		private int current = 0;
		private int line = 1;
		private int col = 1;

		private static readonly Dictionary<string, TokenType> keywords = new Dictionary<string, TokenType>()
		{
			{ "class", TokenType.CLASS },
			{ "this", TokenType.THIS },
			{ "super", TokenType.SUPER },
			{ "var", TokenType.VAR },
			{ "fun", TokenType.FUN },
			{ "nil", TokenType.NIL },
			{ "print", TokenType.PRINT },
			{ "return", TokenType.RETURN },
			{ "and", TokenType.AND },
			{ "or", TokenType.OR },
			{ "if", TokenType.IF },
			{ "else", TokenType.ELSE },
			{ "for", TokenType.FOR },
			{ "while", TokenType.WHILE },
			{ "false", TokenType.FALSE },
			{ "true", TokenType.TRUE }
		};

		Scanner(string source, IErrorReporter errorReporter)
		{
			this.source = new SourceCode(source);
			this.tokens = new TokenList();
			this.errorReporter = errorReporter;
		}

		public string StateAsString()
		{
			return $"Start: {this.tokenStart}, Current: {this.current}, Line: {this.line}, Col: {this.col}";
		}

		public string TokensAsString()
		{
			return this.tokens.ToString();
		}

		public void scanTokens()
		{
			while(!this.IsAtEnd()) {
				this.tokenStart = this.current;
				this.ScanNextToken();
			}
			this.tokens.Add(new Token(TokenType.EOF, "", null, this.line, this.col));
		}

		public void ScanNextToken()
		{
			char c = this.AdvanceCurrent();
			switch (c) {
				case '(': this.AddToken(TokenType.LEFT_PAREN); break;
				case ')': this.AddToken(TokenType.RIGHT_PAREN); break;
				case '{': this.AddToken(TokenType.LEFT_BRACE); break;
				case '}': this.AddToken(TokenType.RIGHT_BRACE); break;
				case ',': this.AddToken(TokenType.COMMA); break;
				case '.': this.AddToken(TokenType.DOT); break;
				case '-': this.AddToken(TokenType.MINUS); break;
				case '+': this.AddToken(TokenType.PLUS); break;
				case ';': this.AddToken(TokenType.SEMICOLON); break;
				case '*': this.AddToken(TokenType.STAR); break;
				case '!': this.AddToken(this.advanceCurrentIfNextMatches('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
				case '=': this.AddToken(this.advanceCurrentIfNextMatches('=') ? TokenType.BANG_EQUAL : TokenType.EQUAL); break;
				case '<': this.AddToken(this.advanceCurrentIfNextMatches('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
				case '>': this.AddToken(this.advanceCurrentIfNextMatches('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
				case '/':
					if (this.advanceCurrentIfNextMatches('/')) {
						while (this.Peek() != '\n' && !this.IsAtEnd()) this.AdvanceCurrent();
					} else if (this.advanceCurrentIfNextMatches('*')) { // begin comment block
						bool commentFinished = false;
						int openLine = this.line;
						int openCol = this.col;
						for (char curr = this.AdvanceCurrent(); !this.IsAtEnd(); curr = this.AdvanceCurrent()) {
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
							this.errorReporter.Error(
								openLine, openCol,
								"Reached end of file while scanning open comment block.",
								this.source.GetLine(openLine - 1),
								"scanning"
							);
						}
					} else {
						this.AddToken(TokenType.SLASH);
					}
					break;
				case '"': this.HandleString(); break;
				case ' ':
				case '\r':
				case '\t':
					break;
				case '\n':
					this.line++;
					this.col = 0;
					break;
				default:
					if (this.IsDigit(c)) {
						this.HandleNumber();
					} else if (this.IsAlpha(c)) {
						this.HandleIdentifier();
					} else {
						this.errorReporter.Error(
							this.line, this.col,
							"Unexpected character '" + c + "'.",
							this.source.GetLine(this.line - 1),
							"scanning"
						);
					}
					break;
			}
		}

		private void HandleIdentifier()
		{
			while (this.IsAlphaNumberic(this.Peek())) this.AdvanceCurrent();
			String text = source.Substring(this.tokenStart, this.current);
			TokenType type = keywords.ContainsKey(text) ? keywords[text] : TokenType.IDENTIFIER;
			this.AddToken(type);
		}

		private void HandleNumber()
		{
			while (this.IsDigit(this.Peek())) this.AdvanceCurrent();
			// fractional part
			if (this.Peek() == '.' && this.IsDigit(this.PeekNext())) {
				// consume the '.'
				this.AdvanceCurrent();
				while (this.IsDigit(this.Peek())) this.AdvanceCurrent();
			}
			this.AddToken(
				TokenType.NUMBER,
				Double.Parse(
					this.source.Substring(this.tokenStart, this.current)
				)
			);
		}

		private void HandleString()
		{
			int beginLine = this.line;
			int beginCol = this.col;
			while (this.Peek() != '"' && !this.IsAtEnd()) {
				if (this.Peek() == '\n') {
					this.line++;
					this.col = 0;
				}
				this.AdvanceCurrent();
			}
			if (this.IsAtEnd()) {
				this.errorReporter.Error(
					beginLine, beginCol,
					"Unterminated string.",
					this.source.GetLine(beginLine - 1),
					"scanning"
				);
				return;
			}
			this.AdvanceCurrent(); // closing '"'
			// trim quotes
			String value = this.source.Substring(this.tokenStart + 1, this.current - 1);
			this.AddToken(TokenType.STRING, value);
		}

		private bool advanceCurrentIfNextMatches(char expected)
		{
			if (this.IsAtEnd()) return false;
			if (this.source.CharAt(this.current) != expected) return false;
			this.current++;
			this.col++;
			return true;
		}

		private char Peek()
		{
			if (this.IsAtEnd()) return '\0';
			return this.source.CharAt(this.current);
		}

		private char PeekNext()
		{
			if (this.current + 1 >= this.source.Length) return '\0';
			return source.CharAt(this.current + 1);
		}

		private bool IsAlpha(char c)
		{
			return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
		}

		private bool IsAlphaNumberic(char c)
		{
			return this.IsAlpha(c) || this.IsDigit(c);
		}

		private bool IsDigit(char c)
		{
			return c >= '0' && c <= '9';
		}

		private bool IsAtEnd()
		{
			return current >= this.source.Length;
		}

		private char AdvanceCurrent()
		{
			this.current++;
			this.col++;
			return this.source.CharAt(this.current - 1);
		}

		private void AddToken(TokenType type)
		{
			this.AddToken(type, null);
		}

		private void AddToken(TokenType type, Object literal)
		{
			String text = this.source.Substring(this.tokenStart, this.current);
			this.tokens.Add(
				new Token(type, text, literal, this.line, this.col)
			);
		}

	}
}