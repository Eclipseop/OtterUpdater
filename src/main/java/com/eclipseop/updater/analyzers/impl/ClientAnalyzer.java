package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.Mask;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Eclipseop.
 * Date: 6/30/2017.
 */
public class ClientAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.name.equals("client")) {
				return new FoundClass(classNode, "Client")
						.addExpectedField("players", "localPlayer", "npcs", "clanMates", "interfaces", "grandExchangeOffers", "energy", "mouseX", "mouseY", "username", "password", "mouseRecorder");
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		/*
		for (ClassNode classNode : getClassNodes()) {
			for (MethodNode methodNode : classNode.methods) {
				if (methodNode.desc.contains(FoundUtil.findClass("RuneScript").getRef().getWrappedName()) ||
						methodNode.desc.contains(FoundUtil.findClass("ScriptEvent").getRef().getWrappedName())) {
					for (AbstractInsnNode ain : methodNode.instructions.toArray()) {
						if (ain instanceof IntInsnNode) {
							int operand = ((IntInsnNode) ain).operand;
							switch (operand) {
								case 3611:
									//System.out.println(methodNode.owner + "." + methodNode.name);
									break;
							}
						}
					}
				}
			}
		}
		*/

		for (ClassNode classNode : getClassNodes()) {
			for (FieldNode fieldNode : classNode.fields) {
				final String desc = fieldNode.desc;

				if (desc.equals("[" + FoundUtil.findClass("Player").getRef().getWrappedName())) {
					foundClass.addFields(new FoundField(fieldNode, "players"));
				} else if (desc.equals(FoundUtil.findClass("Player").getRef().getWrappedName())) {
					foundClass.addFields(new FoundField(fieldNode, "localPlayer"));
				} else if (desc.equals("[" + FoundUtil.findClass("Npc").getRef().getWrappedName())) {
					foundClass.addFields(new FoundField(fieldNode, "npcs"));
				} else if (desc.equals("[" + FoundUtil.findClass("ClanMate").getRef().getWrappedName())) {
					foundClass.addFields(new FoundField(fieldNode, "clanMates"));
				} else if (desc.equals("[[" + FoundUtil.findClass("InterfaceComponent").getRef().getWrappedName())) {
					foundClass.addFields(new FoundField(fieldNode, "interfaces"));
				} else if (desc.equals("[" + FoundUtil.findClass("GrandExchangeOffer").getRef().getWrappedName())) {
					foundClass.addFields(new FoundField(fieldNode, "grandExchangeOffers"));
				} else if (desc.equals(FoundUtil.findClass("MouseRecorder").getRef().getWrappedName())) {
					foundClass.addFields(new FoundField(fieldNode, "mouseRecorder"));
				}
			}
		}

		boolean usernameFound = false;
		boolean passwordFound = false;
		login:
		for (ClassNode classNode : getClassNodes()) {
			for (MethodNode methodNode : classNode.methods) {
				if (methodNode.access != Opcodes.ACC_STATIC || !methodNode.desc.startsWith("(" + FoundUtil.findClass("GameEngine").getRef().getWrappedName())) {
					continue;
				}

				final List<List<AbstractInsnNode>> all = Mask.findAll(
						methodNode,
						Mask.GETSTATIC.describe("Ljava/lang/String;"),
						Mask.INVOKEVIRTUAL
				);
				if (all != null) {
					for (List<AbstractInsnNode> abstractInsnNodes : all) {
						for (AbstractInsnNode ain : abstractInsnNodes) {
							if (ain instanceof MethodInsnNode) {
								final MethodInsnNode min = (MethodInsnNode) ain;
								if (min.name.equals("length")) {
									int length = -1;
									if (min.getNext() instanceof IntInsnNode) {
										length = ((IntInsnNode) min.getNext()).operand;
									}

									String hookName;
									if (length == 320 && !usernameFound) {
										hookName = "username";
										usernameFound = true;
									} else if (length == 20 && !passwordFound) {
										hookName = "password";
										passwordFound = true;
									} else {
										continue;
									}

									if (min.getPrevious() instanceof FieldInsnNode) {
										final FieldInsnNode fin = (FieldInsnNode) min.getPrevious();

										foundClass.addFields(new FoundField(foundClass.findField(fin), hookName));

										if (passwordFound && usernameFound) {
											break login;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		final List<MethodNode> energyMethods =
				foundClass.getRef().methods.stream()
						.filter(p -> p.access == 24)
						.filter(p -> p.desc.startsWith("(" + FoundUtil.findClass("InterfaceComponent").getRef().getWrappedName()))
						.collect(Collectors.toList());
		energy:
		for (MethodNode methodNode : energyMethods) {
			final List<List<AbstractInsnNode>> all = Mask.findAll(methodNode, Mask.BIPUSH.operand(11), Mask.IF_ICMPNE, Mask.GETSTATIC);
			if (all == null) {
				continue;
			}
			for (List<AbstractInsnNode> abstractInsnNodes : all) {
				for (AbstractInsnNode ain : abstractInsnNodes) {
					if (ain instanceof FieldInsnNode) {
						foundClass.addFields(new FoundField(foundClass.findField((FieldInsnNode) ain), "energy"));
						break energy;
					}
				}
			}
		}

		final List<MethodNode> gamestateMethods =
				foundClass.getRef().methods.stream().filter(p -> p.access == 0 && p.desc.equals("()V")).collect(Collectors.toList());
		gamestate:
		for (MethodNode methodNode : gamestateMethods) {
			final List<List<AbstractInsnNode>> abstractInsnNodes = Mask.findAll(methodNode, Mask.LDC, Mask.LDC, Mask.INVOKEVIRTUAL, Mask.LDC, Mask.PUTSTATIC);
			if (abstractInsnNodes == null) {
				continue;
			}
			for (List<AbstractInsnNode> abstractInsnNode : abstractInsnNodes) {
				boolean cool = false;
				for (AbstractInsnNode ain : abstractInsnNode) {
					if (ain instanceof LdcInsnNode) {
						final Object ob = ((LdcInsnNode) ain).cst;
						if (ob instanceof String && ob.equals("js5crc")) {
							cool = true;
						}
					}
				}

				if (cool) {
					for (AbstractInsnNode ain : abstractInsnNode) {
						if (ain instanceof FieldInsnNode) {
							foundClass.addFields(new FoundField(foundClass.findField((FieldInsnNode) ain), "gamestate"));
							break gamestate;
						}
					}
				}
			}
		}

		for (ClassNode classNode : getClassNodes()) {
			if (classNode.name.equals(FoundUtil.findClass("MouseRecorder").getRef().name)) {
				for (MethodNode methodNode : classNode.methods) {
					if (methodNode.name.equals("run")) {
						boolean found = false;
						for (AbstractInsnNode ain : methodNode.instructions.toArray()) {
							if (ain instanceof FieldInsnNode) {
								final FieldInsnNode fin = (FieldInsnNode) ain;
								if (!fin.desc.equals("I") || fin.owner.equals(classNode.name)) {
									continue;
								}

								String name;
								if (found) {
									name = "Y";
								} else {
									name = "X";
									found = true;
								}

								foundClass.addFields(new FoundField(foundClass.findField(fin), "mouse" + name));
							}
						}
					}
				}
			}
		}
	}
}

