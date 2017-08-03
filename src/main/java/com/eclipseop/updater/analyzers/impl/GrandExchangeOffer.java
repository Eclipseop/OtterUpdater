package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 8/1/2017.
 */
public class GrandExchangeOffer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.fieldCount("B", true) == 1 && classNode.fieldCount("I", true) == 5) {
				return new FoundClass(classNode, "GrandExchangeOffer");
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {

	}
}
