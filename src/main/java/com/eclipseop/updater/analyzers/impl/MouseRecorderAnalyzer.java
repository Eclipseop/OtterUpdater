package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 8/1/2017.
 */
public class MouseRecorderAnalyzer extends Analyzer {
	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.fieldCount("Ljava/lang/Object;", true) == 1 && classNode.fieldCount("[I", true) == 2) {
				//Bootstrap.getBuilder().addClass(classNode.name, "running", "index", "queueX", "queueY", "lock").putName("OtterUpdater", "MouseRecorder");
				return new FoundClass(classNode, "MouseRecorder").addExpectedField("running", "index", "lock", "queueX", "queueY");
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

			if (fieldNode.desc.equals("Z")) {
				foundClass.addFields(new FoundField(fieldNode, "running"));
			} else if (fieldNode.desc.equals("I")) {
				foundClass.addFields(new FoundField(fieldNode, "index"));
			} else if (fieldNode.desc.equals("Ljava/lang/Object;")) {
				foundClass.addFields(new FoundField(fieldNode, "lock"));
			}
		}

		final boolean[] found = {false};
		for (MethodNode methodNode : foundClass.getRef().methods) {
			if (methodNode.name.equals("run")) {
				for (AbstractInsnNode ain : methodNode.instructions.toArray()) {
					if (ain instanceof FieldInsnNode) {
						final FieldInsnNode fin = (FieldInsnNode) ain;
						if (!(fin).desc.equals("[I")) {
							continue;
						}

						String suffix;
						if (found[0]) {
							suffix = "Y";
						} else {
							suffix = "X";
							found[0] = true;
						}

						foundClass.addFields(new FoundField(foundClass.findField(fin), "queue" + suffix));
					}
				}
			}
		}
	}
}
