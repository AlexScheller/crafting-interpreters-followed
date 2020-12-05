using System;

namespace CSharpLox
{
    class ConsoleErrorReporter : IErrorReporter
    {

        private bool _Errored = false;
        public bool Errored { get => this._Errored; }

        public void Error(int line, int col, string message, string lineText, string type) {
			this.Report(line, col, "", message, lineText, type);
            this._Errored = true;
		}

		private void Report(
			int line, int col, string where, string message, string lineText, string type
		) {
			void WriteToErrorWithColor(
				string error, ConsoleColor foregroundColor, bool newLine = false
			) {
				Console.ForegroundColor = foregroundColor;
				if (newLine) { Console.Error.WriteLine(error); } else { Console.Error.Write(error); }
				Console.ResetColor();
			}
			WriteToErrorWithColor("Error ", ConsoleColor.Red);
			WriteToErrorWithColor($"({type}) ", ConsoleColor.Yellow);
			Console.Error.Write($"{where}: ");
			WriteToErrorWithColor(message, ConsoleColor.Green, true);
			string location = $"{line}, {col} | ";
			WriteToErrorWithColor(location, ConsoleColor.Blue);
			Console.Error.WriteLine(lineText);
            int columns = location.Length + (col - 1);
			Console.Error.Write(new String(' ', columns));
			WriteToErrorWithColor("^", ConsoleColor.Red, true);
		}
    }
}