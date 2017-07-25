package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/24/2017.
 */
public class PickableDecorAnalyzer extends Analyzer { //the stack on the ground
	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.fieldCount("L" + Bootstrap.getBuilder().findByName("Entity").getClassObsName() + ";") == 3)
				.findFirst()
				.ifPresent(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name).putName("OtterUpdater", "PickableDecor");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {

	}
}
