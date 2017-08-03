package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 6/30/2017.
 */
public class NodeAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.fieldCount("J", true) == 1 && classNode.fieldCount(classNode.getWrappedName(), true) == 2) {
				return new FoundClass(classNode, "Node").addExpectedField("key", "next", "previous");
			}
		}
		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode field : foundClass.getRef().fields) {
			if (Modifier.isStatic(field.access)) {
				continue;
			}

			if (field.desc.equals("J")) {
				foundClass.addFields(new FoundField(field, "key"));
			} else if (field.desc.equals(foundClass.getRef().getWrappedName())) {
				if (Modifier.isPublic(field.access)) {
					foundClass.addFields(new FoundField(field, "next"));
				} else {
					foundClass.addFields(new FoundField(field, "previous"));
				}
			}
		}
	}
}
