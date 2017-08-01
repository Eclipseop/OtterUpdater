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
public class ClanMateAnalyzer extends Analyzer {
	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findByName("Node").getClassObsName()))
				.filter(p -> p.fieldCount("B", true) == 1)
				.forEach(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "world", "rank", "name").putName("OtterUpdater", "ClanMate");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		final boolean[] foundName = {false};
		classNode.fields.stream()
				.filter(p -> !Modifier.isStatic(p.access))
				.forEach(field -> {
					final String desc = field.desc;
					if (desc.equals("I")) {
						Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "world");
					} else if (desc.equals("B")) {
						Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "rank");
					} else if (desc.equals("Ljava/lang/String;")) {
						if (!foundName[0]) {
							Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "name");
							foundName[0] = true;
						}
					}
				});
	}
}
