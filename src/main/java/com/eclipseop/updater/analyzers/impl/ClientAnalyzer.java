package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

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
					Bootstrap.getBuilder().addClass(c.name, "players", "localPlayer", "npcs", "clanMates", "interfaces").putName("OtterUpdater", "Client");
				});
		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		final String playerName = Bootstrap.getBuilder().findByName("Player").getClassObsName();

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
