package lox;

import java.util.List;

abstract class Expression {

	static class Binary extends Expression {

		final Expression left;
		final Token operator;
		final Expression right;

		Expression (Expression left, Token operator, Expression right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

	}

}