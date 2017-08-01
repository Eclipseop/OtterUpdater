package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 6/30/2017.
 */
public class NodeAnaylzer extends Analyzer {

	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.fieldCount("J", true) == 1 && p.fieldCount("L" + p.name + ";", true) == 2)
				.forEach(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "key", "next", "previous").putName("OtterUpdater", "Node");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.fields.stream().filter(p -> !Modifier.isStatic(p.access)).forEach(c -> {
			if (c.desc.equals("J")) {
				Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "key");
			}

			if (c.desc.contains(classNode.name)) {
				if (Modifier.isPublic(c.access)) {
					Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "next");
				} else {
					Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "previous");
				}
			}
		});
	}
}
