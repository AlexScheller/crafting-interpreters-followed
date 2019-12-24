package lox;

import java.util.Arrays;
import java.util.List;

public class SourceCode {

	private final String source;
	private final List<String> lines;

	public SourceCode(String source) {
		this.source = source;
		this.lines = Arrays.asList(source.split(System.lineSeparator()));
	}

	// For use in interpreted mode.
	public void appendLine(String line) {
		this.lines.add(line);
	}

	public int length() {
		return this.source.length();
	}

	public String toString() {
		return this.source;
	}

	public char charAt(int index) {
		return this.source.charAt(index);
	}

	public String substring(int begin, int end) {
		return this.source.substring(begin, end);
	}

	public String getLine(int index) {
		if (index < 0 || index >= this.lines.size()) {
			throw new IndexOutOfBoundsException(index);
		} else {
			return this.lines.get(index);
		}
	}

}