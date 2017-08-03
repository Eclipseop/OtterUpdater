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
public class NpcAnalyzer extends Analyzer {
	/*
	@Override
	public ClassNode identifyClass(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findClass("Actor").getClassObsName()))
				.filter(p -> p.getFieldTypeCount(true) == 1)
				.forEach(c -> {
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
	*/
	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.superName.equals(FoundUtil.findClass("Actor").getRef().name)) {
				if (classNode.getFieldTypeCount(true) == 1) {
					return new FoundClass(classNode, "Npc");
				}
			}
		}
		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode fieldNode : foundClass.getRef().fields) {
			if (!Modifier.isStatic(fieldNode.access)) {
				foundClass.addFields(new FoundField(fieldNode, "definition"));
				break;
			}
		}
	}
}
