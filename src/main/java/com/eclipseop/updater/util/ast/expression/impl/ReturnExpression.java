package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class ReturnExpression extends Expression {

	private Expression value;

	public ReturnExpression(AbstractInsnNode ref, Expression value) {
		super(ref);
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

	@Override
	public boolean consumesStack() {
		return true;
	}
}
