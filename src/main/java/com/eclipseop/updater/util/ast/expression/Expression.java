package com.eclipseop.updater.util.ast.expression;

import org.objectweb.asm.tree.AbstractInsnNode;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public abstract class Expression {

	private AbstractInsnNode ref;

	public Expression(AbstractInsnNode ref) {
		this.ref = ref;
	}

	public AbstractInsnNode getRef() {
		return ref;
	}

	public abstract boolean consumesStack();
}
