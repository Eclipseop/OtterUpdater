package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/23/2017.
 */
public class ClanMateAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.superName.equals(FoundUtil.findClass("Node").getRef().name)) {
				if (classNode.fieldCount("B", true) == 1) {
					return new FoundClass(classNode, "ClanMate").addExpectedField("world", "rank", "name");
				}
			}
		}
		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {

		boolean foundName = false;
		for (FieldNode fieldNode : foundClass.getRef().fields) {
			if (!Modifier.isStatic(fieldNode.access)) {
				final String desc = fieldNode.desc;
				if (desc.equals("I")) {
					foundClass.addFields(new FoundField(fieldNode, "world"));
				} else if (desc.equals("B")) {
					foundClass.addFields(new FoundField(fieldNode, "rank"));
				} else if (desc.equals("Ljava/lang/String;")) {
					if (!foundName) {
						foundClass.addFields(new FoundField(fieldNode, "name"));
						foundName = true;
					}
				}
			}
		}
	}
	/*
	@Override
	public ClassNode identifyClass(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findClass("Node").getClassObsName()))
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
	*/
}
