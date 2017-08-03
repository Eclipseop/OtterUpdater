package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 8/1/2017.
 */
public class FontAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (Modifier.isFinal(classNode.access)) {
				for (MethodNode methodNode : classNode.methods) {
					if (methodNode.name.equals("<init>") && methodNode.desc.equals("([B[I[I[I[I[I[[B)V")) {
						return new FoundClass(classNode, "Font");
					}
				}
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {

	}
}
