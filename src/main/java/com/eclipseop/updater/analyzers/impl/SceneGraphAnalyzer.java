package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/2/2017.
 */
public class SceneGraphAnalyzer extends Analyzer {

	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.fieldCount("[[[I", true) == 2)
				.filter(p -> p.fieldCount("[[I", true) == 2)
				.findFirst()
				.ifPresent(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "tiles").putName("OtterUpdater", "SceneGraph");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.fields.stream()
				.filter(p -> p.desc.equals("[[[L" + Bootstrap.getBuilder().findByName("Tile").getClassObsName() + ";"))
				.findFirst()
				.ifPresent(c -> {
					Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "tiles");
				});
	}
}
