package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class ArrayAccessExpression extends Expression { //AALOAD = ref, index -> value

	private Expression array;
	private Expression index;

	public ArrayAccessExpression(AbstractInsnNode ref, Expression array, Expression index) {
		super(ref);
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

	@Override
	public boolean consumesStack() {
		return true;
	}
}
