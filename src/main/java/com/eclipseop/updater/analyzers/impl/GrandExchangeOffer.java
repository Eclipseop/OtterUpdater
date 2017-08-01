package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 8/1/2017.
 */
public class GrandExchangeOffer extends Analyzer {
	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.fieldCount("B", true) == 1)
				.filter(p -> p.fieldCount("I", true) == 5)
				.forEach(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name).putName("OtterUpdater", "GrandExchangeOffer");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {

	}
}
