package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/22/2017.
 */
public class PlayerAnalyzer extends Analyzer {
	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findByName("Actor").getClassObsName()))
				.filter(p -> p.fields.size() > 5)
				.findFirst()
				.ifPresent(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "name", "actions").putName("OtterUpdater", "Player");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.fields.stream().filter(p -> !Modifier.isStatic(p.access)).forEach(field -> {
			if (field.desc.equals("Ljava/lang/String;")) {
				Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "name");
			} else if (field.desc.equals("[Ljava/lang/String;")) {
				Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "actions");
			}
		});
	}
}
