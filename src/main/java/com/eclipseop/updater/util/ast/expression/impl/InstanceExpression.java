package com.eclipseop.updater.util.ast.expression.impl;

import com.eclipseop.updater.util.ast.expression.Expression;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

/**
 * Created by Eclipseop.
 * Date: 7/28/2017.
 */
public class InstanceExpression extends Expression { //GETFIELD = ref -> value

	private Expression objectRef;

	public InstanceExpression(AbstractInsnNode ref, Expression objectRef) {
		super(ref);
		this.objectRef = objectRef;
	}

	public String getFieldName() {
		final FieldInsnNode fin = (FieldInsnNode) getRef();

		return fin.owner + "." + fin.name;
	}

	public Expression getObjectRef() {
		return objectRef;
	}

	@Override
	public String toString() {
		return "InstanceExpression{" +
				"objectRef=" + objectRef +
				'}';
	}

	@Override
	public boolean consumesStack() {
		return true;
	}
}
