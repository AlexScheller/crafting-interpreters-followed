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

	public static void error(int line, int col, String message) {
		report(line, col, "", message);
	}

	private static void report(int line, int col, String where, String message) {
		System.err.println("<" + line + ":" + col + "> Error " + where + ": " + message);
		hadError = true;
	}

}