using System;
using System.IO;
using System.Text;

namespace CSharpLox
{
	public class Lox
	{

		static bool hadError = false;

		static void Main(string[] args)
		{
			if (args.Length > 1) {
				Console.WriteLine("Usage: cshlox <script>");
				Environment.Exit(64);
			} else if (args.Length == 1) {
				RunFile(args[0]);
			} else {
				RunInteractive();
			}
		}
		
		static void RunFile(String path)
		{
			if (File.Exists(path)) {
				string contents = File.ReadAllText(path);
				Run(contents);
				if (hadError) { Environment.Exit(64); }
			}
		}

		static void RunInteractive()
		{
			String line = null;
			Console.Write(":> ");
			while ((line = Console.ReadLine()) != null) {
				Run(line);
				Console.Write(":> ");
				hadError = false;
			}
		}

		static void Run(String contents)
		{
			Console.WriteLine(contents);
		}

		public static void Error(int line, int col, string message, string lineText, string type) {
			Report(line, col, "", message, lineText, type);
		}

		static void Report(
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
			StringBuilder sb = new StringBuilder();
			// This can be a single loop with arithmetic
			for (int i = 0; i < location.Length; i++) {
				sb.Append(' ');
			}
			for (int i = 1; i < col; i++) {
				sb.Append(' ');
			}
			Console.Error.Write(sb.ToString());
			WriteToErrorWithColor("^", ConsoleColor.Red, true);
			hadError = true;
		}

	}
}
