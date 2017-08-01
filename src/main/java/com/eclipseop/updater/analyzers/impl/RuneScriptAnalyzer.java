package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/24/2017.
 */
public class RuneScriptAnalyzer extends Analyzer {
	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findByName("DoublyNode").getClassObsName()))
				.filter(p -> p.fieldCount("I", true) == 4)
				.filter(p -> p.fieldCount("[I", true) == 2)
				.filter(p -> p.fieldCount("[Ljava/lang/String;", true) == 1)
				.forEach(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name).putName("OtterUpdater", "RuneScript");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {

	}
}
