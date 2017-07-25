package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/23/2017.
 */
public class NpcAnalyzer extends Analyzer {
	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findByName("Actor").getClassObsName()))
				.filter(p -> p.getFieldTypeCount(true) == 1)
				.findFirst()
				.ifPresent(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "definition").putName("OtterUpdater", "Npc");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.fields.stream().filter(p -> !Modifier.isStatic(p.access)).findFirst().ifPresent(c -> {
			Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "definition");
		});
	}
}
