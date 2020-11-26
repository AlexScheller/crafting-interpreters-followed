using System;
using System.IO;

namespace CSharpLox
{
	class Lox
	{
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
			if (File.Exists(path))
			{
				string contents = File.ReadAllText(path);
				Run(contents);
			}
		}

		static void RunInteractive()
		{
			String line = null;
			Console.Write(":> ");
			while ((line = Console.ReadLine()) != null)
			{
				Run(line);
				Console.Write(":> ");
			}
		}

		static void Run(String contents)
		{
			Console.WriteLine(contents);
		}

	}
}
