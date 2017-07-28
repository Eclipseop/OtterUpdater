package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class VarExpression extends Expression {

	private String varName;

	public VarExpression(String varName) {
		this.varName = varName;
	}

	public String getVarName() {
		return varName;
	}

	@Override
	public String toString() {
		return "VarExpression{" +
				"varName='" + varName + '\'' +
				'}';
	}
}
