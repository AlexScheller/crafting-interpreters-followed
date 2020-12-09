using System;

namespace CSharpLox
{
	public abstract class Expression
	{
		public interface IExpressionVisitor<T>
		{
			public T VisitBinaryExpression(Binary expression);
			public T VisitGroupingExpression(Grouping expression);
			public T VisitLiteralExpression(Literal expression);
			public T VisitUnaryExpression(Unary expression);
		}

		public abstract T Accept<T>(IExpressionVisitor<T> visitor);

		class Binary : Expression
		{
			readonly Expression left;
			readonly Token operation;
			readonly Expression right;

			Binary (Expression left, Token operation, Expression right) {
				this.left = left;
				this.operation = operation;
				this.right = right;
			}
			public override T Accept<T>(IExpressionVisitor<T> visitor) {
				return visitor.VisitBinaryExpression(this);
			}
		}

		class Grouping : Expression
		{
			readonly Expression expression;

			Grouping (Expression expression) {
				this.expression = expression;
			}
			public override T Accept<T>(IExpressionVisitor<T> visitor) {
				return visitor.VisitGroupingExpression(this);
			}
		}

		class Literal : Expression
		{
			readonly Object value;

			Literal (Object value) {
				this.value = value;
			}
			public override T Accept<T>(IExpressionVisitor<T> visitor) {
				return visitor.VisitLiteralExpression(this);
			}
		}

		class Unary : Expression
		{
			readonly Token operation;
			readonly Expression right;

			Unary (Token operation, Expression right) {
				this.operation = operation;
				this.right = right;
			}
			public override T Accept<T>(IExpressionVisitor<T> visitor) {
				return visitor.VisitUnaryExpression(this);
			}
		}

	}
}