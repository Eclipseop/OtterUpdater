package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.ast.AbstractSyntaxTree;
import com.eclipseop.updater.util.ast.expression.Expression;
import com.eclipseop.updater.util.ast.expression.impl.ConditionExpression;
import com.eclipseop.updater.util.ast.expression.impl.IntegerExpression;
import com.eclipseop.updater.util.ast.expression.impl.MathExpression;
import com.eclipseop.updater.util.ast.expression.impl.VarExpression;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eclipseop.
 * Date: 6/30/2017.
 */
public class ClientAnalyzer extends Analyzer {

	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];
		classNodes.stream()
				.filter(p -> p.name.equals("client"))
				.findFirst()
				.ifPresent(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "players", "localPlayer", "npcs", "clanMates", "interfaces", "gamestate").putName("OtterUpdater", "Client");
				});
		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		final String playerName = Bootstrap.getBuilder().findByName("Player").getClassObsName();

		final List<Expression> expressions = AbstractSyntaxTree.find(Arrays.asList(classNode), Opcodes.IF_ICMPEQ);
		String gamestate = null;
		for (Expression expression : expressions) { // TODO: 7/28/2017 do somehting so this isn't fucking cancer
			if (gamestate != null) {
				Bootstrap.getBuilder().addField(classNode.name, gamestate.split("\\.")[1]).putName("OtterUpdater", "gamestate");
				break;
			}

			if (expression instanceof ConditionExpression) {
				final ConditionExpression conditionExpression = (ConditionExpression) expression;
				if (conditionExpression.getLeft() instanceof IntegerExpression || conditionExpression.getRight() instanceof IntegerExpression) {
					if (conditionExpression.getLeft() instanceof IntegerExpression) {
						if (((IntegerExpression) conditionExpression.getLeft()).getOperand() != 20) {
							continue;
						}
					} else {
						if (((IntegerExpression) conditionExpression.getRight()).getOperand() != 20) {
							continue;
						}
					}

					if (conditionExpression.getLeft() instanceof MathExpression) {
						final MathExpression mathExpression = (MathExpression) conditionExpression.getLeft();
						if (mathExpression.getLeft() instanceof VarExpression) {
							if (((VarExpression) mathExpression.getLeft()).getVarName().contains(".")) {
								gamestate = ((VarExpression) mathExpression.getLeft()).getVarName();
							}
						} else if (mathExpression.getRight() instanceof VarExpression) {
							if (((VarExpression) mathExpression.getRight()).getVarName().contains(".")) {
								gamestate = ((VarExpression) mathExpression.getRight()).getVarName();
							}
						}
					} else if (conditionExpression.getRight() instanceof MathExpression) {
						final MathExpression mathExpression = (MathExpression) conditionExpression.getRight();
						if (mathExpression.getLeft() instanceof VarExpression) {
							if (((VarExpression) mathExpression.getLeft()).getVarName().contains(".")) {
								gamestate = ((VarExpression) mathExpression.getLeft()).getVarName();
							}
						} else if (mathExpression.getRight() instanceof VarExpression) {
							if (((VarExpression) mathExpression.getRight()).getVarName().contains(".")) {
								gamestate = ((VarExpression) mathExpression.getRight()).getVarName();
							}
						}
					}
				}
			}
		}

		getClassNodes().forEach(node -> {
			node.fields.forEach(field -> {
				final String desc = field.desc;

				if (desc.equals("[L" + playerName + ";")) {
					Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "players");
				} else if (desc.equals("L" + playerName + ";")) {
					Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "localPlayer").putStatic(node.name);
				} else if (desc.equals("[L" + Bootstrap.getBuilder().findByName("Npc").getClassObsName() + ";")) {
					Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "npcs");
				} else if (desc.equals("[L" + Bootstrap.getBuilder().findByName("ClanMate").getClassObsName() + ";")) {
					Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "clanMates").putStatic(node.name);
				} else if (desc.equals("[[L" + Bootstrap.getBuilder().findByName("InterfaceComponent").getClassObsName() + ";")) {
					Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "interfaces").putStatic(node.name);
				}
			});

			/*
			node.methods.forEach(method -> {
				Arrays.stream(method.instructions.toArray()).forEach(ain -> {
					final FieldInsnNode ain1 = (FieldInsnNode) ain;

				});
			});
			*/
		});


	}
}
