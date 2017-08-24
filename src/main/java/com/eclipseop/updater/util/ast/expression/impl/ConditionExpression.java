package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;
import com.eclipseop.updater.util.ast.expression.Tree;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Created by Eclipseop.
 * Date: 7/28/2017.
 */
public class ConditionExpression extends Expression implements Tree { //most the time: value, value ->

	private Expression left;
	private Expression right;

	public ConditionExpression(AbstractInsnNode ref, Expression left, Expression right) {
		super(ref);
		this.left = left;
		this.right = right;
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
		String operation = null;
		final int opcode = getRef().opcode();

		if (opcode == Opcodes.IF_ICMPNE || opcode == Opcodes.IFNONNULL) {
			operation = "!=";
		} else if (opcode == Opcodes.IF_ICMPGT) {
			operation = ">";
		} else if (opcode == Opcodes.IF_ICMPGE) {
			operation = ">=";
		} else if (opcode == Opcodes.IF_ICMPEQ) {
			operation = "==";
		} else if (opcode == Opcodes.IF_ICMPLE) {
			operation = "<=";
		} else if (opcode == Opcodes.IF_ICMPLT) {
			operation = "<";
		}

		return operation;
	}

	@Override
	public boolean consumesStack() {
		return true;
	}
}
