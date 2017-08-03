package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;
import com.eclipseop.updater.util.ast.expression.Tree;

/**
 * Created by Eclipseop.
 * Date: 7/28/2017.
 */
public class ConditionExpression extends Expression implements Tree {

	private Expression left;
	private Expression right;
	private String operation;

	public ConditionExpression(Expression left, Expression right, String operation) {
		this.left = left;
		this.right = right;
		this.operation = operation;
	}

	@Override
	public Expression getRight() {
		return right;
	}

	@Override
	public Expression getLeft() {
		return left;
	}

	public String getOperation() {
		return operation;
	}

	@Override
	public String toString() {
		return "ConditionExpression{" +
				"left=" + left +
				", right=" + right +
				", operation='" + operation + '\'' +
				'}';
	}
}
