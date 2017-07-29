package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.Mask;
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
					Bootstrap.getBuilder().addClass(c.name, "players", "localPlayer", "npcs", "clanMates", "interfaces", "gamestate").putName("OtterUpdater", "Client");
				});
		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		final String playerName = Bootstrap.getBuilder().findByName("Player").getClassObsName();

		final List<MethodNode> methodList =
				getClassNodes().stream().filter(p -> p.name.equals("client")).findFirst().get().methods.stream().filter(p -> p.access == 0 && p.desc.equals("()V")).collect(Collectors.toList());

		for (MethodNode methodNode : methodList) {
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
		});
	}
}
