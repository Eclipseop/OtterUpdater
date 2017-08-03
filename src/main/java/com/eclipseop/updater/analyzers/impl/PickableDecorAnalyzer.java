package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/24/2017.
 */
public class PickableDecorAnalyzer extends Analyzer { //the stack on the ground

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.fieldCount(FoundUtil.findClass("Entity").getRef().getWrappedName(), true) == 3) {
				return new FoundClass(classNode, "PickableDecor");
			}
		}
		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {

	}
}
