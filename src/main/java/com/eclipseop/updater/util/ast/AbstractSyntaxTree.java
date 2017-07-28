package com.eclipseop.updater.util.ast;


import com.eclipseop.updater.util.ast.expression.Expression;
import com.eclipseop.updater.util.ast.expression.impl.*;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Eclipseop.
 * Date: 7/27/2017.
 */
public class AbstractSyntaxTree {

	public static final Pattern OBJECT_PATTERN = Pattern.compile("L.+?;");

	public static List<Expression> find(final List<ClassNode> list, final int opcodeToFind) {
		final List<Expression> tempList = new ArrayList<>();

		list.stream().forEach(node -> {
			node.methods.stream().forEach(method -> {
				final List<Expression> stack = new LinkedList<>();

				Arrays.stream(method.instructions.toArray()).forEach(ain -> {
					final int opcode = ain.opcode();

					if (opcode == -1) {
						return;
					}

					Expression lastExpression = null;
					if (stack.size() > 0) {
						lastExpression = stack.get(stack.size() - 1);
					}
					switch (opcode) {
						case Opcodes.GOTO:
							final JumpInsnNode jin = (JumpInsnNode) ain;
							//System.out.println(jin.label.getLabel().getOffset());
							break;
						case Opcodes.NEWARRAY:
							stack.add(new VarExpression("Array" + ((IntInsnNode) ain).operand));
							break;
						case Opcodes.NEW:
							stack.add(new VarExpression(((TypeInsnNode) ain).desc));
							break;
						case Opcodes.GETFIELD:
							final FieldInsnNode fieldFin = (FieldInsnNode) ain;
							stack.add(new InstanceExpression(lastExpression, fieldFin.owner + "." + fieldFin.name));
							stack.remove(lastExpression);
							break;
						case Opcodes.GETSTATIC:
							final FieldInsnNode staticFin = (FieldInsnNode) ain;
							stack.add(new VarExpression(staticFin.owner + "." + staticFin.name));
							break;
						case Opcodes.LLOAD:
						case Opcodes.ALOAD:
						case Opcodes.ILOAD:
							String loadPrefix;
							if (opcode == Opcodes.ASTORE) {
								loadPrefix = "A";
							} else {
								loadPrefix = "I";
							}

							stack.add(new VarExpression(loadPrefix + "field" + ((VarInsnNode) ain).var));
							break;
						case Opcodes.AASTORE:
							if (stack.size() >= 3) {
								final Expression array = stack.get(stack.size() - 3);
								final Expression index = stack.get(stack.size() - 2);

								stack.add(new ArrayStoreExpression(array, index, lastExpression));

								stack.remove(array);
								stack.remove(index);
								stack.remove(lastExpression);
							}
							break;
						case Opcodes.AALOAD:
							final Expression loadArray = stack.get(stack.size() - 2);
							stack.add(new ArrayAccessExpression(loadArray, lastExpression));
							stack.remove(loadArray);
							stack.remove(lastExpression);
							break;
						case Opcodes.DLOAD:
						case Opcodes.ASTORE:
						case Opcodes.ISTORE:
							String storePrefix;
							if (opcode == Opcodes.ASTORE) {
								storePrefix = "A";
							} else if (opcode == Opcodes.ISTORE) {
								storePrefix = "I";
							} else {
								storePrefix = "D";
							}

							stack.add(new FieldExpression(storePrefix + "field" + ((VarInsnNode) ain).var, lastExpression));
							stack.remove(lastExpression);
							break;
						case Opcodes.LDC:
							stack.add(new VarExpression(String.valueOf(((LdcInsnNode) ain).cst)));
							break;
						case Opcodes.SIPUSH:
						case Opcodes.BIPUSH:
							stack.add(new IntegerExpression(((IntInsnNode) ain).operand));
							break;
						case Opcodes.ICONST_M1:
						case Opcodes.ICONST_0:
						case Opcodes.ICONST_1:
						case Opcodes.ICONST_2:
						case Opcodes.ICONST_3:
						case Opcodes.ICONST_4:
						case Opcodes.ICONST_5:
							stack.add(new IntegerExpression(opcode - Opcodes.ICONST_0));
							break;
						case Opcodes.ACONST_NULL:
							stack.add(new VarExpression("null"));
							break;
						case Opcodes.IMUL:
						case Opcodes.IDIV:
						case Opcodes.IADD:
						case Opcodes.ISUB:
							if (stack.size() >= 2) {
								final Expression left = stack.get(stack.size() - 2);

								String operator;
								if (opcode == Opcodes.IADD) {
									operator = "+";
								} else if (opcode == Opcodes.ISUB) {
									operator = "-";
								} else if (opcode == Opcodes.IDIV) {
									operator = "/";
								} else {
									operator = "*";
								}

								stack.add(new MathExpression(operator, left, lastExpression));

								stack.remove(left);
								stack.remove(lastExpression);
							}
							break;
							/*
							case Opcodes.INVOKESPECIAL:
							case Opcodes.INVOKESTATIC:
								final MethodInsnNode min = (MethodInsnNode) ain;
								String param = min.desc.split("\\(")[1].split("\\)")[0];

								//System.out.println(param);

								List<String> objects = new ArrayList<>();
								final Matcher matcher = OBJECT_PATTERN.matcher(param);
								while (matcher.find()) {
									objects.add(matcher.group());
									param = param.replace(matcher.group(), "");
								}

								//System.out.println(param);
								int paramSize = objects.size() + param.replaceAll("\\[", "").length();

								Expression[] expressions = new Expression[paramSize];
								for (int i = 0; i < paramSize; i++) {
									final Expression removedExp = stack.get(stack.size() - 1);
									expressions[i] = removedExp;
									stack.remove(removedExp);
								}

								stack.add(new InvokeExpression(min.desc.split("\\)")[1], expressions));
								break;
								*/
						case Opcodes.IRETURN:
							stack.add(new ReturnExpression(lastExpression));
							stack.remove(lastExpression);
							break;
						case Opcodes.IFEQ:
							stack.remove(lastExpression);
							break;
						case Opcodes.IF_ICMPEQ:
						case Opcodes.IF_ICMPNE:
						case Opcodes.IF_ICMPGT:
						case Opcodes.IF_ICMPGE:
								/*
								stack.remove(stack.size() - 2);
								stack.remove(lastExpression);
								*/
							String operation = "";
							if (opcode == Opcodes.IF_ICMPNE) {
								operation = "!=";
							} else if (opcode == Opcodes.IF_ICMPGT) {
								operation = ">";
							} else if (opcode == Opcodes.IF_ICMPGE) {
								operation = ">=";
							} else if (opcode == Opcodes.IF_ICMPEQ) {
								operation = "==";
							}

							if (stack.size() >= 2) {
								stack.add(new ConditionExpression(stack.get(stack.size() - 2), lastExpression, operation));
							}
							break;

						default:
							break;
					}
					//System.out.println(Printer.OPCODES[opcode]);

					if (opcodeToFind == opcode) {
						tempList.add(stack.get(stack.size() - 1));
					}
				});
			});

		});
		return tempList;
	}
}
