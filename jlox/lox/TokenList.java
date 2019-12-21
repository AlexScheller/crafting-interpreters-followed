package lox;

import java.util.ArrayList;
import java.util.List;

import java.util.Iterator;

public class TokenList {

	private final List<Token> tokens;

	TokenList() {
		this.tokens = new ArrayList<>();
	}

	public int size() {
		return this.tokens.size();
	}

	public String asString() {
		StringBuilder sb = new StringBuilder();
		Iterator<Token> iter = this.tokens.iterator();
		sb.append('[');
		while (iter.hasNext()) {
			sb.append(iter.next().asString());
			if (iter.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append(']');
		return sb.toString();
	}

	public void addToken(Token t) {
		this.tokens.add(t);
	}

}