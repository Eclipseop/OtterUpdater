package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/2/2017.
 */
public class EntityAnalyzer extends Analyzer {

	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];
		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findByName("DoublyNode").getClassObsName()))
				.filter(p -> Modifier.isAbstract(p.access))
				.forEach(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "height").putName("OtterUpdater", "Entity");
				});
		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.fields.stream()
				.filter(p -> p.desc.equals("I"))
				.filter(p -> !Modifier.isStatic(p.access))
				.findFirst()
				.ifPresent(c -> {
					Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "height");
				});
	}
}
