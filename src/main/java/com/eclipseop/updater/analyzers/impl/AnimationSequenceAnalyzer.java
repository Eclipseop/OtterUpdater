package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/23/2017.
 */
public class AnimationSequenceAnalyzer extends Analyzer {
	
	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.superName.endsWith(FoundUtil.findClass("DoublyNode").getRef().name)) {
				if (classNode.fieldCount("[I", true) == 5) {
					return new FoundClass(classNode, "AnimationSequence");
				}
			}
		}
		
		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {

	}
}
