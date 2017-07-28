package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;

/**
 * Created by Eclipseop.
 * Date: 7/28/2017.
 */
public class InstanceExpression extends Expression {

	private Expression objectRef;
	private String fieldName;

	public InstanceExpression(Expression objectRef, String fieldName) {
		this.objectRef = objectRef;
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public Expression getObjectRef() {
		return objectRef;
	}

	@Override
	public String toString() {
		return "InstanceExpression{" +
				"objectRef=" + objectRef +
				", fieldName='" + fieldName + '\'' +
				'}';
	}
}
