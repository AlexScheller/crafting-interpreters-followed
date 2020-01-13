package lox;

import java.util.List;

abstract class Expression {

	interface Visitor<R> {

		R visitBinaryExpression(Binary expression);

	}

	abstract <R> R accept(Visitor<R> visitor);

	static class Binary extends Expression {

		final Expression left;
		final Token operator;
		final Expression right;

		Expression (Expression left, Token operator, Expression right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBinaryExpression(this);
		}

	}

}