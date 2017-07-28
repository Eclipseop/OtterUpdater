package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class ArrayStoreExpression extends Expression {

	private Expression array;
	private Expression index;
	private Expression value;

	public ArrayStoreExpression(Expression array, Expression index, Expression value) {
		this.array = array;
		this.index = index;
		this.value = value;
	}

	public Expression getIndex() {
		return index;
	}

	public Expression getArray() {
		return array;
	}

	public Expression getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "ArrayStoreExpression{" +
				"array=" + array +
				", index=" + index +
				", value=" + value +
				'}';
	}
}
