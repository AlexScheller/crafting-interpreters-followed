package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

	static boolean hadError = false;
	
	public static void main(String[] args) throws IOException {
		if (args.length > 1) {
			System.out.println("Usage: jlox <script>");
			System.exit(64);
		} else if (args.length == 1) {
			runFile(args[0]);
		} else {
			runInteractive();
		}
	}

	private static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));
		if (hadError) System.exit(65);
	}

	private static void runInteractive() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);

		while(true) {
			System.out.print(">) ");
			run(reader.readLine());
			hadError = false;
		}
	}

	private static void run(String source) {
		Scanner scanner = new Scanner(source);
		scanner.scanTokens();
		System.out.println(scanner.tokensAsString());
	}

	public static void error(int line, int col, String message, String lineText, String type) {
		report(line, col, "", message, lineText, type);
	}

	private static void report(int line, int col, String where, String message, String lineText, String type) {
		// TODO: Better color handling, or maybe a whole error handling suite...
		String ANSI_RED = "\u001B[31m";
		String ANSI_GREEN = "\u001B[32m";
		String ANSI_YELLOW = "\u001B[33m";
		String ANSI_BLUE = "\u001B[34m";
		String ANSI_RESET = "\u001B[0m";
		String errorPrefix = ANSI_RED + "Error " + ANSI_RESET;
		String errorType = ANSI_YELLOW + "(" + type + ") " + ANSI_RESET;
		String errorMessage = ANSI_GREEN + message + ANSI_RESET;
		System.err.println(errorPrefix + errorType + where + ": " + errorMessage);
		String location = line + ", " + col;
		System.err.println(ANSI_BLUE + location + " | " + ANSI_RESET + lineText);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < location.length(); i++) {
			sb.append(' ');
		}
		sb.append("   ");
		for (int i = 1; i < col; i++) {
			sb.append(' ');
		}
		sb.append(ANSI_RED + "^" + ANSI_RESET);
		System.err.println(sb.toString());
		hadError = true;
	}

}