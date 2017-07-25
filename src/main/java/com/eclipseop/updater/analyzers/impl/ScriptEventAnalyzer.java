package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/24/2017.
 */
public class ScriptEventAnalyzer extends Analyzer {
	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findByName("Node").getClassObsName()))
				.filter(p -> p.fieldCount("[Ljava/lang/Object;", true) == 1)
				.filter(p -> p.fieldCount("I", true) == 6)
				.findFirst()
				.ifPresent(c -> {
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
}
