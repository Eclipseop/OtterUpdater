package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 8/1/2017.
 */
public class BufferAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.superName.equals(FoundUtil.findClass("Node").getRef().name)) {
				if (classNode.fieldCount("[B", true) == 1 && classNode.fieldCount("I", true) == 1) {
					for (MethodNode p : classNode.methods) {
						if (p.name.equals("<init>") && p.desc.equals("([B)V")) {
							return new FoundClass(classNode, "Buffer").addExpectedField("payload", "caret");
						}
					}
				}
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode fieldNode : foundClass.getRef().fields) {
			if (Modifier.isStatic(fieldNode.access)) {
				continue;
			}

			final String desc = fieldNode.desc;

			if (desc.equals("I")) {
				foundClass.addFields(new FoundField(fieldNode, "caret"));
			} else if (desc.equals("[B")) {
				foundClass.addFields(new FoundField(fieldNode, "payload"));
			}
		}
	}
}
