package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class FieldExpression extends Expression {

	private String fieldName;
	private Expression value;

	public FieldExpression(String fieldName, Expression value) {
		this.fieldName = fieldName;
		this.value = value;
	}

	public Expression getValue() {
		return value;
	}

	public String getFieldName() {
		return fieldName;
	}

	@Override
	public String toString() {
		return "FieldExpression{" +
				"fieldName='" + fieldName + '\'' +
				", value=" + value +
				'}';
	}
}
