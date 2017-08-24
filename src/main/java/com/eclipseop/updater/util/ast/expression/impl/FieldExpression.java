package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class FieldExpression extends Expression { //ISTORE = value ->

	private Expression value;

	public FieldExpression(AbstractInsnNode ref, Expression value) {
		super(ref);
		this.value = value;
	}

	public Expression getValue() {
		return value;
	}

	public String getFieldName() {
		String storePrefix;
		final int opcode = getRef().opcode();

		if (opcode == Opcodes.ASTORE) {
			storePrefix = "A";
		} else if (opcode == Opcodes.ISTORE) {
			storePrefix = "I";
		} else {
			storePrefix = "D";
		}

		return storePrefix + "field" + ((VarInsnNode) getRef()).var;
	}

	@Override
	public String toString() {
		return "FieldExpression{" +
				"value=" + value +
				'}';
	}

	@Override
	public boolean consumesStack() {
		return true;
	}
}
