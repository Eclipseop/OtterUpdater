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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eclipseop.
 * Date: 7/24/2017.
 */
public class InterfaceComponentAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.superName.equals(FoundUtil.findClass("Node").getRef().name)) {
				if (classNode.fieldCount("[Ljava/lang/Object;", true) > 10) {
					return new FoundClass(classNode, "InterfaceComponent").addExpectedField("components", "parent", "text");
				}
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode fieldNode : foundClass.getRef().fields) {
			if (fieldNode.desc.equals("[" + foundClass.getRef().getWrappedName())) {
				foundClass.addFields(new FoundField(fieldNode, "components"));
			} else if (fieldNode.desc.equals(foundClass.getRef().getWrappedName())) {
				foundClass.addFields(new FoundField(fieldNode, "parent"));
			}
		}

		final List<AbstractInsnNode> textMask = Mask.find(foundClass.getRef(), Mask.INVOKEVIRTUAL, Mask.PUTFIELD.own(foundClass.getRef().name).describe("Ljava/lang/String;"));
		if (textMask != null) {
			for (AbstractInsnNode ain : textMask) {
				if (ain instanceof FieldInsnNode) {
					foundClass.addFields(new FoundField(foundClass.findField((FieldInsnNode) ain), "text"));
					break;
				}
			}
		}
	}
}
