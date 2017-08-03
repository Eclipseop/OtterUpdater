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
 * Date: 7/22/2017.
 */
public class PlayerAnalyzer extends Analyzer {
	/*
	@Override
	public ClassNode identifyClass(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findClass("Actor").getClassObsName()))
				.filter(p -> p.fields.size() > 5)
				.forEach(c -> {
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
	*/
	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (Modifier.isFinal(classNode.access) && classNode.superName.equals(FoundUtil.findClass("Actor").getRef().name)) {
				return new FoundClass(classNode, "Player").addExpectedField("name", "actions");
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode fieldNode : foundClass.getRef().fields) {
			if (!Modifier.isStatic(fieldNode.access)) {
				if (fieldNode.desc.equals("Ljava/lang/String;")) {
					foundClass.addFields(new FoundField(fieldNode, "name"));
				} else if (fieldNode.desc.equals("[Ljava/lang/String;")) {
					foundClass.addFields(new FoundField(fieldNode, "actions"));
				}
			}
		}
	}
}
