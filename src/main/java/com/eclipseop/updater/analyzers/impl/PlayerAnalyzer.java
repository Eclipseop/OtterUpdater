package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.ast.AbstractSyntaxTree;
import com.eclipseop.updater.util.ast.expression.Expression;
import com.eclipseop.updater.util.ast.expression.impl.*;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eclipseop.
 * Date: 7/22/2017.
 */
public class PlayerAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (Modifier.isFinal(classNode.access) && classNode.superName.equals(FoundUtil.findClass("Actor").getRef().name)) {
				return new FoundClass(classNode, "Player").addExpectedField("name", "actions", "totalLevel", "combatLevel");
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode fieldNode : foundClass.getRef().fields) {
			if (!Modifier.isStatic(fieldNode.access)) {
				if (fieldNode.desc.equals("Ljava/lang/String;")) {
					foundClass.addFields(new FoundField(fieldNode, "name"));
				} else if (fieldNode.desc.equals("[Ljava/lang/String;")) {
					foundClass.addFields(new FoundField(fieldNode, "actions"));
				}
			}
		}

		/*
		boolean combatFound = false;
		boolean totalFound = false;
		level:
		for (MethodNode methodNode : foundClass.getRef().methods) {
			if (methodNode.access != Opcodes.ACC_FINAL || !methodNode.desc.startsWith("(" + FoundUtil.findClass("Buffer").getRef().getWrappedName())) {
				continue;
			}

			final List<List<AbstractInsnNode>> all = Mask.findAll(
					methodNode,
					Mask.BIPUSH,
					Mask.INVOKEVIRTUAL,
					Mask.PUTFIELD.describe("I")
			);
			if (all == null) {
				continue;
			}

			int operand = -1;
			for (List<AbstractInsnNode> abstractInsnNodes : all) {
				for (AbstractInsnNode ain : abstractInsnNodes) {
					if (ain instanceof IntInsnNode) {
						operand = ((IntInsnNode) ain).operand;
						continue;
					}

					if (ain instanceof FieldInsnNode) {
						String hookName = null;
						if (operand == -109 && !totalFound) {
							hookName = "totalLevel";
							totalFound = true;
						} else if (operand == -113 && !combatFound) {
							hookName = "combatLevel";
							combatFound = true;
						} else {
							continue;
						}

						foundClass.addFields(new FoundField(foundClass.findField((FieldInsnNode) ain), hookName));

						if (combatFound && totalFound) {
							break level;
						}
					}
				}
			}

		}
		*/

		//totalLevel:
		boolean totalLevelFound = false;
		boolean combatLevelFound = false;
		levels:
		for (ClassNode classNode : getClassNodes()) {
			for (MethodNode methodNode : classNode.methods) {
				if (methodNode.access != 24 || !methodNode.desc.startsWith("(" + foundClass.getRef().getWrappedName() + "III")) {
					continue;
				}

				if (!totalLevelFound) {
					final List<Expression> expressions = AbstractSyntaxTree.find(methodNode, Opcodes.IFNE, Opcodes.IF_ICMPNE);
					for (Expression expression : expressions) {
						final ConditionExpression ce = (ConditionExpression) expression;
						if (ce.isExpectedExpressions(IntegerExpression.class, MathExpression.class)) {
							final IntegerExpression intExp = (IntegerExpression) ce.find(IntegerExpression.class);
							if (intExp.getOperand() != 0) {
								continue;
							}

							final MathExpression me = (MathExpression) ce.find(MathExpression.class);
							final InstanceExpression ie = (InstanceExpression) me.find(InstanceExpression.class);

							foundClass.addFields(new FoundField(FoundUtil.findField(ie.getFieldName()), "totalLevel"));
							totalLevelFound = true;
						}
					}
				}

				if (!combatLevelFound) {
					final List<Expression> expressions = AbstractSyntaxTree.find(methodNode, Opcodes.IF_ICMPGE, Opcodes.IF_ICMPLE);

					for (Expression expression : expressions) {
						final ConditionExpression ce = (ConditionExpression) expression;
						if (ce.isExpectedExpressions(MathExpression.class, MathExpression.class)) {
							final MathExpression me = (MathExpression) ce.getLeft();
							if (me.containsExpression(InstanceExpression.class)) {
								final InstanceExpression ie = (InstanceExpression) me.find(InstanceExpression.class);

								foundClass.addFields(new FoundField(FoundUtil.findField(ie.getFieldName()), "combatLevel"));
								combatLevelFound = true;
							}
						}
					}
				}

				if (combatLevelFound && totalLevelFound) {
					break levels;
				}
			}
		}
	}
}
