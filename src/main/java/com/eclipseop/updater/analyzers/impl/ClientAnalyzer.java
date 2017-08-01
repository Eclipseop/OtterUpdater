package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.Mask;
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

	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];
		classNodes.stream()
				.filter(p -> p.name.equals("client"))
				.findFirst()
				.ifPresent(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "players", "localPlayer", "npcs", "clanMates", "interfaces", "gamestate", "energy", "username").putName("OtterUpdater", "Client");
				});
		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		final String playerName = Bootstrap.getBuilder().findByName("Player").getClassObsName();

		//System.out.println(("(L" + Bootstrap.getBuilder().findByName("GameEngine").getClassObsName()));
		/*
		final List<MethodNode> passwordMethods =
				classNode.methods.stream().filter(p -> p.access == Opcodes.ACC_STATIC && p.desc.contains("(L" + Bootstrap.getBuilder().findByName("GameEngine").getClassObsName())).collect(Collectors.toList());
		*/


		getClassNodes().stream().map(m -> m.methods).forEach(m -> {
			final List<MethodNode> passwordMethods = m.stream().filter(p -> p.access == Opcodes.ACC_STATIC && p.desc.startsWith("(L" + Bootstrap.getBuilder().findByName("GameEngine").getClassObsName() + ";")).collect(Collectors.toList());
			for (MethodNode methodNode : passwordMethods) {
				//System.out.println(methodNode.owner + "." + methodNode.name + "()");
				final List<List<AbstractInsnNode>> all = Mask.findAll(methodNode, Mask.GETSTATIC.describe("Ljava/lang/String;"), Mask.INVOKEVIRTUAL.describe("()Ljava/lang/String;"), Mask.IFNE);
				if (all == null) {
					continue;
				}
				for (List<AbstractInsnNode> abstractInsnNodes : all) {
					for (AbstractInsnNode ain : abstractInsnNodes) {
						if (ain instanceof FieldInsnNode) {
							if (((FieldInsnNode) ain).owner.equals(methodNode.owner.name)) {
								//System.out.println(((FieldInsnNode) ain).owner + "." + ((FieldInsnNode) ain).name);
								Bootstrap.getBuilder().addField(classNode.name, ((FieldInsnNode) ain).name).putStatic(((FieldInsnNode) ain).owner).putName("OtterUpdater", "username");
							}
							//}
							//Bootstrap.getBuilder().addField(classNode.name, ((FieldInsnNode) ain).name).putStatic(((FieldInsnNode) ain).owner).putName("OtterUpdater", "password");
						}
					}
				}
			}
		});


		/*
		System.out.println(passwordMethods.size());
		for (MethodNode methodNode : passwordMethods) {
			//System.out.println(methodNode.desc);
			final List<List<AbstractInsnNode>> all = Mask.findAll(methodNode, Mask.LDC.cst(""), Mask.PUTSTATIC);
			if (all == null) {
				continue;
			}
			for (List<AbstractInsnNode> abstractInsnNodes : all) {
				for (AbstractInsnNode ain : abstractInsnNodes) {
					if (ain instanceof FieldInsnNode) {
						System.out.println(((FieldInsnNode) ain).owner + "." + ((FieldInsnNode) ain).name);
					}
				}
			}
		}
		*/

		/*
		getClassNodes().forEach(node -> {
			node.methods.forEach(method -> {
				Arrays.stream(method.instructions.toArray()).forEach(ain -> {
					if (ain instanceof FieldInsnNode) {
						if (((FieldInsnNode) ain).owner.equals("cu") && ((FieldInsnNode) ain).name.equals("ac")) {
							System.out.println(node.name + "." + method.name + "()");
						}
					}
				});
			});
		});
		*/

		//AbstractSyntaxTree.find(getClassNodes(), )


		final List<MethodNode> gamestateMethods =
				classNode.methods.stream().filter(p -> p.access == 0 && p.desc.equals("()V")).collect(Collectors.toList());
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
					for (AbstractInsnNode insnNode : abstractInsnNode) {
						if (insnNode instanceof FieldInsnNode) {
							Bootstrap.getBuilder().addField("client", ((FieldInsnNode) insnNode).name).putName("OtterUpdater", "gamestate");
							break gamestate;
						}
					}
				}
			}
		}

		final List<MethodNode> energyMethods =
				classNode.methods.stream().filter(p -> p.desc.startsWith("(L" + Bootstrap.getBuilder().findByName("InterfaceComponent").getClassObsName() + ";")).collect(Collectors.toList()); // TODO: 7/29/2017 check access
		energy:
		for (MethodNode methodNode : energyMethods) {
			final List<List<AbstractInsnNode>> all = Mask.findAll(methodNode, Mask.BIPUSH.operand(11), Mask.IF_ICMPNE, Mask.GETSTATIC);
			if (all == null) {
				continue;
			}
			for (List<AbstractInsnNode> abstractInsnNodes : all) {
				for (AbstractInsnNode ain : abstractInsnNodes) {
					if (ain instanceof FieldInsnNode) {
						Bootstrap.getBuilder().addField(classNode.name, ((FieldInsnNode) ain).name).putName("OtterUpdater", "energy");
						break energy;
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
		});
	}
}
