package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/1/2017.
 */
public class DoublyNodeAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.superName.equals(FoundUtil.findClass("Node").getRef().name) &&
					classNode.fieldCount(classNode.getWrappedName(), true) == 2) {
				return new FoundClass(classNode, "DoublyNode").addExpectedField("next", "previous");
			}
		}
		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		String temp = null;
		for (MethodNode method : foundClass.getRef().methods) {
			if (Modifier.isStatic(method.access)) {
				continue;
			}

			for (AbstractInsnNode ain : method.instructions.toArray()) {
				if (ain instanceof FieldInsnNode) {
					final FieldInsnNode fin = (FieldInsnNode) ain;

					if (temp == null) {
						foundClass.addFields(new FoundField(foundClass.findField(fin), "previous"));
						temp = fin.name;
					} else if (!temp.equals(fin.name)) {
						foundClass.addFields(new FoundField(foundClass.findField(fin), "next"));
						break;
					}
				}
			}
			break;
		}
	}
}
