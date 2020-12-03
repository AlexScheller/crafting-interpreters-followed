using System;
using System.Collections.Generic;

namespace CSharpLox
{
	class SourceCode
	{

		public int Length { get => this.source.Length; }

		private readonly string source;
		private readonly List<string> lines;

		public SourceCode(string source)
		{
			this.source = source;
			this.lines = new List<string>(source.Split(Environment.NewLine));
		}

		// For use in interpreted mode.
		public void AppendLine(string line)
		{
			this.lines.Add(line);
		}

		public override string ToString()
		{
			return this.source;
		}

		// Maybe this should just override the operator?
		public char CharAt(int index)
		{
			return this.source[index];
		}

		public string Substring(int begin, int end)
		{
			return this.source.Substring(begin, end);
		}

		public string GetLine(int index)
		{
			if (index < 0 || index >= this.lines.Count) {
				throw new IndexOutOfRangeException(index.ToString());
			} else {
				return this.lines[index];
			}
		}

	}
}