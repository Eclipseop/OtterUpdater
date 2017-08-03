package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import com.eclipseop.updater.util.ast.AbstractSyntaxTree;
import com.eclipseop.updater.util.ast.expression.Expression;
import com.eclipseop.updater.util.ast.expression.impl.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eclipseop.
 * Date: 7/2/2017.
 */
public class ActorAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.superName.equals(FoundUtil.findClass("Entity").getRef().name)) {
				if (Modifier.isAbstract(classNode.access)) {
					return new FoundClass(classNode, "Actor").addExpectedField("hitsplatCount").addExpectedField("strictX", "strictY", "animation");
				}
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode field : foundClass.getRef().fields) {
			if (Modifier.isStatic(field.access)) {
				continue;
			}

			if (field.desc.equals("B")) {
				foundClass.addFields(new FoundField(field, "hitsplatCount"));
			}
		}

		boolean xFound = false;
		position:
		for (ClassNode classNode : getClassNodes()) {
			for (MethodNode method : classNode.methods) {
				if (method.desc.startsWith("(" + foundClass.getRef().getWrappedName()) && method.desc.endsWith(")V")) {
					if (Modifier.isFinal(method.access) && Modifier.isStatic(method.access)) {
						if (method.count(Opcodes.GETFIELD) == 2) {
							for (AbstractInsnNode ain : method.instructions.toArray()) {
								if (ain instanceof FieldInsnNode) {
									final FieldInsnNode fin = (FieldInsnNode) ain;
									if (!xFound) {
										foundClass.addFields(new FoundField(foundClass.findField(fin), "strictX"));
										xFound = true;
									} else {
										foundClass.addFields(new FoundField(foundClass.findField(fin), "strictY"));
									}
								}
							}
							break position;
						}
					}
				}
			}
		}

		animation:
		for (ClassNode classNode : getClassNodes()) {
			for (MethodNode method : classNode.methods) {
				if (method.access != 24) { //static final
					continue;
				}
				if (method.desc.startsWith("(" + FoundUtil.findClass("Actor").getRef().getWrappedName())) {
					final List<Expression> expressions = AbstractSyntaxTree.find(method, Opcodes.IF_ICMPEQ);

					if (expressions.stream().filter(p -> ((ConditionExpression) p).isExpectedExpressions(MathExpression.class, IntegerExpression.class)).count() != 1) {
						continue;
					}
					for (Expression expression : expressions) {
						final ConditionExpression ce = (ConditionExpression) expression;
						if (ce.isExpectedExpressions(MathExpression.class, IntegerExpression.class)) {
							if (((IntegerExpression) ce.find(IntegerExpression.class)).getOperand() != -1) {
								continue;
							}

							final MathExpression me = (MathExpression) ce.find(MathExpression.class);
							if (me.isExpectedExpressions(InstanceExpression.class, VarExpression.class)) {
								final InstanceExpression ie = (InstanceExpression) me.find(InstanceExpression.class);
								foundClass.addFields(new FoundField(FoundUtil.findField(ie.getFieldName()), "animation"));
								break animation;
							}
						}
					}
				}
			}
		}
	}
}
