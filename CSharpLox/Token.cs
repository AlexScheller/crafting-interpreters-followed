using System;

namespace CSharpLox
{
	public class Token
	{

		readonly TokenType type;
		readonly string lexeme;
		readonly Object literal;
		readonly int line;
		readonly int col;

		public Token(TokenType type, String lexeme, Object literal, int line, int col)
		{
			this.type = type;
			this.lexeme = lexeme;
			this.literal = literal;
			this.line = line;
			// note that the column is of the first character of the token.
			this.col = col;
		}

		public override string ToString()
		{
			return $"<{type} {lexeme} {literal}>";
		}

	}
}