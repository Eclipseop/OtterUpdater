package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class ArrayStoreExpression extends Expression { //AASTORE = ref, index, value ->

	private Expression array;
	private Expression index;
	private Expression value;

	public ArrayStoreExpression(AbstractInsnNode ref, Expression array, Expression index, Expression value) {
		super(ref);
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

	@Override
	public boolean consumesStack() {
		return true;
	}
}
