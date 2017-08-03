package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import com.eclipseop.updater.util.Mask;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eclipseop.
 * Date: 7/23/2017.
 */
public class NpcDefinitionAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.superName.equals(FoundUtil.findClass("DoublyNode").getRef().name)) {
				if (classNode.fieldCount("I", true) == 18) {
					return new FoundClass(classNode, "NpcDefinition").addExpectedField("name", "actions", "id");
				}
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode fieldNode : foundClass.getRef().fields) {
			if (!Modifier.isStatic(fieldNode.access)) {
				if (fieldNode.desc.equals("Ljava/lang/String;")) {
					foundClass.addFields(new FoundField(fieldNode, "name"));
				} else if (fieldNode.desc.equals("[Ljava/lang/String;")) {
					foundClass.addFields(new FoundField(fieldNode, "actions"));
				}
			}
		}

		final List<AbstractInsnNode> idMask = Mask.find(foundClass.getRef(), Mask.GETFIELD.describe("I"), Mask.I2L.distance(5));
		for (AbstractInsnNode p : idMask) {
			if (p instanceof FieldInsnNode) {
				foundClass.addFields(new FoundField(foundClass.findField((FieldInsnNode) p), "id"));
				break;
			}
		}

	}
}
