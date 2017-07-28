package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class MathExpression extends Expression {

	private String operator;
	private Expression left;
	private Expression right;

	public MathExpression(String operator, Expression left, Expression right) {
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

	public String getOperator() {
		return operator;
	}

	@Override
	public String toString() {
		return "MathExpression{" +
				left + " " + operator + " " + right +
				'}';
	}
}
