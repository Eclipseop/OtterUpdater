package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class ReturnExpression extends Expression {

	private Expression value;

	public ReturnExpression(Expression value) {
		this.value = value;
	}

	public Expression getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "ReturnExpression{" +
				"value=" + value +
				'}';
	}
}
