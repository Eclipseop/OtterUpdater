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
				return new FoundClass(classNode, "Client").addExpectedField("players", "localPlayer", "npcs", "clanMates", "interfaces", "grandExchangeOffers", "username", "energy", "mouseX", "mouseY");
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		getClassNodes().forEach(node -> {
			node.fields.forEach(field -> {
				final String desc = field.desc;

				if (desc.equals("[" + FoundUtil.findClass("Player").getRef().getWrappedName())) {
					//Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "players");
					foundClass.addFields(new FoundField(field, "players"));
				} else if (desc.equals(FoundUtil.findClass("Player").getRef().getWrappedName())) {
					foundClass.addFields(new FoundField(field, "localPlayer"));
					//Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "localPlayer").putStatic(node.name);
				} else if (desc.equals("[" + FoundUtil.findClass("Npc").getRef().getWrappedName())) {
					foundClass.addFields(new FoundField(field, "npcs"));
					//Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "npcs");
				} else if (desc.equals("[" + FoundUtil.findClass("ClanMate").getRef().getWrappedName())) {
					foundClass.addFields(new FoundField(field, "clanMates"));
					//Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "clanMates").putStatic(node.name);
				} else if (desc.equals("[[" + FoundUtil.findClass("InterfaceComponent").getRef().getWrappedName())) {
					foundClass.addFields(new FoundField(field, "interfaces"));
					//Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "interfaces").putStatic(node.name);
				} else if (desc.equals("[" + FoundUtil.findClass("GrandExchangeOffer").getRef().getWrappedName())) {
					//Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "grandExchangeOffers");
					foundClass.addFields(new FoundField(field, "grandExchangeOffers"));
				}
			});
		});

		username:
		for (ClassNode classNode : getClassNodes()) {
			List<MethodNode> methodNodes = classNode.methods;
			final List<MethodNode> passwordMethods =
					methodNodes.stream().filter(p -> p.access == Opcodes.ACC_STATIC && p.desc.startsWith("(" + FoundUtil.findClass("GameEngine").getRef().getWrappedName())).collect(Collectors.toList());

			for (MethodNode methodNode : passwordMethods) {
				final List<List<AbstractInsnNode>> all = Mask.findAll(methodNode, Mask.GETSTATIC.describe("Ljava/lang/String;"), Mask.INVOKEVIRTUAL.describe("()Ljava/lang/String;"), Mask.IFNE);
				if (all == null) {
					continue;
				}
				for (List<AbstractInsnNode> abstractInsnNodes : all) {
					for (AbstractInsnNode ain : abstractInsnNodes) {
						if (ain instanceof FieldInsnNode) {
							if (((FieldInsnNode) ain).owner.equals(methodNode.owner.name)) {
								//System.out.println(((FieldInsnNode) ain).owner + "." + ((FieldInsnNode) ain).name);
								foundClass.addFields(new FoundField(foundClass.findField((FieldInsnNode) ain), "username")); // TODO: 8/2/2017 suppose to be password, wtf why xd
								break username;
							}
						}
					}
				}
			}
		}

		final List<MethodNode> energyMethods =
				foundClass.getRef().methods.stream()
						.filter(p -> p.desc.startsWith("(" + FoundUtil.findClass("InterfaceComponent").getRef().getWrappedName()))
						.collect(Collectors.toList()); // TODO: 7/29/2017 check access
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

