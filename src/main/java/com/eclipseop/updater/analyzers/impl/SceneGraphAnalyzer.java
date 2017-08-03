package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/2/2017.
 */
public class SceneGraphAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.fieldCount("[[[I", true) == 2 && classNode.fieldCount("[[I", true) == 2) {
				return new FoundClass(classNode, "SceneGraph").addExpectedField("tiles");
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode field : foundClass.getRef().fields) {
			if (field.desc.equals("[[[" + FoundUtil.findClass("Tile").getRef().getWrappedName())) {
				foundClass.addFields(new FoundField(field, "tiles"));
			}
		}
	}
}
