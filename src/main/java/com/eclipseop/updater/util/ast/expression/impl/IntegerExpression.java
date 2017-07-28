package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class IntegerExpression extends Expression {

	private int operand;

	public IntegerExpression(int operand) {
		this.operand = operand;
	}

	public int getOperand() {
		return operand;
	}

	@Override
	public String toString() {
		return "IntegerExpression{" +
				"operand=" + operand +
				'}';
	}
}
