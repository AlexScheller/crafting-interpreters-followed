using System;
using System.IO;
using System.Text;

namespace CSharpLox
{
	public class Lox
	{

		// static bool hadError = false;
		// Resources/Services
		static IErrorReporter errorReporter = new ConsoleErrorReporter();

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
				if (errorReporter.Errored) { Environment.Exit(64); }
			}
		}

		static void RunInteractive()
		{
			String line = null;
			Console.Write(":> ");
			while ((line = Console.ReadLine()) != null) {
				Run(line);
				Console.Write(":> ");
				// hadError = false;
			}
		}

		static void Run(String contents)
		{
			Console.WriteLine(contents);
		}

	}
}
