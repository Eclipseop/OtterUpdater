package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class IntegerExpression extends Expression {

	public IntegerExpression(AbstractInsnNode ref) {
		super(ref);
	}

	public int getOperand() {
		if (getRef() == null) {
			return 0;
		}

		if (getRef().opcode() >= 2 && getRef().opcode() <= 8) {
			return getRef().opcode() - Opcodes.ICONST_0;
		}

		return ((IntInsnNode) getRef()).operand;
	}

	@Override
	public boolean consumesStack() {
		return false;
	}
}
