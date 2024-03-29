package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/2/2017.
 */
public class EntityAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (!Modifier.isAbstract(classNode.access)) {
				continue;
			}

			if (classNode.superName.equals(FoundUtil.findClass("DoublyNode").getRef().name)) {
				return new FoundClass(classNode, "Entity").addExpectedField("height");
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode field : foundClass.getRef().fields) {
			if (!Modifier.isStatic(field.access)) {
				if (field.desc.equals("I")) {
					foundClass.addFields(new FoundField(field, "height"));
				}
			}
		}
	}
}
