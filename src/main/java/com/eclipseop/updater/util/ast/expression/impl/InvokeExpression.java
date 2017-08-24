package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.Arrays;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class InvokeExpression extends Expression {

	private String returnType;
	private Expression[] params;

	public InvokeExpression(AbstractInsnNode ref, String returnType, Expression[] params) {
		super(ref);
		this.returnType = returnType;
		this.params = params;
	}

	public Expression[] getParams() {
		return params;
	}

	public String getReturnType() {
		return returnType;
	}

	@Override
	public String toString() {
		return "InvokeExpression{" +
				"returnType='" + returnType + '\'' +
				", params=" + Arrays.toString(params) +
				'}';
	}

	@Override
	public boolean consumesStack() {
		return true;
	}
}
