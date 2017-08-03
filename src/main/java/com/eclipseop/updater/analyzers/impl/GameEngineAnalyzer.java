package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/22/2017.
 */
public class GameEngineAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.superName.contains("applet")) {
				return new FoundClass(classNode, "GameEngine").addExpectedField("canvas");
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode field : foundClass.getRef().fields) {
			if (field.desc.contains("Canvas")) {
				foundClass.addFields(new FoundField(field, "canvas"));
			}
		}
	}
	/*
	@Override
	public ClassNode identifyClass(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.contains("applet"))
				.forEach(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "canvas").putName("OtterUpdater", "GameEngine");
				});
		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.fields.stream().filter(p -> p.desc.contains("Canvas")).findFirst().ifPresent(c -> {
			Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "canvas");
		});
	}
	*/
}
