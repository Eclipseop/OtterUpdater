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
 * Date: 7/24/2017.
 */
public class PickableNodeAnalyzer extends Analyzer { //the actual item

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.superName.equals(FoundUtil.findClass("Entity").getRef().name))  {
				if (classNode.fieldCount("I", true) == 2) {
					return new FoundClass(classNode, "PickableNode").addExpectedField("name", "stackSize");
				}
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (MethodNode method : foundClass.getRef().methods) {
			if (method.name.equals("<init>") || Modifier.isStatic(method.access)) {
				continue;
			}

			boolean foundName = false;
			for (AbstractInsnNode ain : method.instructions.toArray()) {
				if (ain instanceof FieldInsnNode) {
					final FieldInsnNode fin = (FieldInsnNode) ain;

					if (!foundName) {
						foundClass.addFields(new FoundField(foundClass.findField(fin), "name"));
						foundName = true;
					} else {
						foundClass.addFields(new FoundField(foundClass.findField(fin), "stackSize"));
					}
				}
			}
			break;
		}
	}
}
