package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class ArrayAccessExpression extends Expression {

	private Expression array;
	private Expression index;

	public ArrayAccessExpression(Expression array, Expression index) {
		this.array = array;
		this.index = index;
	}

	public Expression getArray() {
		return array;
	}

	public Expression getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return "ArrayAccessExpression{" +
				"array=" + array +
				", index=" + index +
				'}';
	}
}
