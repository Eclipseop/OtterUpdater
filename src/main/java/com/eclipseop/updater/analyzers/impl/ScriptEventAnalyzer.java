package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/24/2017.
 */
public class ScriptEventAnalyzer extends Analyzer {
	/*
	@Override
	public ClassNode identifyClass(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findClass("Node").getClassObsName()))
				.filter(p -> p.fieldCount("[Ljava/lang/Object;", true) == 1)
				.filter(p -> p.fieldCount("I", true) == 6)
				.forEach(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "args").putName("OtterUpdater", "ScriptEvent");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.fields.stream()
				.filter(p -> p.desc.equals("[Ljava/lang/Object;"))
				.findFirst()
				.ifPresent(c -> {
					Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "args");
				});
	}
	*/
	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.superName.equals(FoundUtil.findClass("Node").getRef().name)) {
				if (classNode.fieldCount("[Ljava/lang/Object;", true) == 1) {
					if (classNode.fieldCount("I", true) == 6) {
						return new FoundClass(classNode, "ScriptEvent").addExpectedField("args");
					}
				}
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode fieldNode : foundClass.getRef().fields) {
			if (fieldNode.desc.equals("[Ljava/lang/Object;")) {
				foundClass.addFields(new FoundField(fieldNode, "args"));
				break;
			}
		}
	}
}
