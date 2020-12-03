using System;
using System.Collections.Generic;

namespace CSharpLox
{
	public class TokenList
	{
		public int Count { get => this.tokens.Count; }

		private readonly List<Token> tokens;

		public TokenList()
		{
			this.tokens = new List<Token>();
		}

		public override string ToString()
		{
			return $"[{String.Join(", ", this.tokens)}]";
		}

		public void Add(Token t)
		{
			this.tokens.Add(t);
		}

	}
}