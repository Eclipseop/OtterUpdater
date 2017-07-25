package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.Mask;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eclipseop.
 * Date: 7/23/2017.
 */
public class NpcDefinitionAnalyzer extends Analyzer {

	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findByName("DoublyNode").getClassObsName()))
				.filter(p -> p.fieldCount("I", true) == 18)
				.findFirst()
				.ifPresent(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "name", "actions", "id").putName("OtterUpdater", "NpcDefinition");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.fields.stream().filter(p -> !Modifier.isStatic(p.access)).forEach(c -> {
			if (c.desc.equals("Ljava/lang/String;")) {
				Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "name");
			} else if (c.desc.equals("[Ljava/lang/String;")) {
				Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "actions");
			}
		});

		final List<AbstractInsnNode> idMask = Mask.find(classNode, Mask.GETFIELD.describe("I"), Mask.I2L.distance(5));
		idMask.stream().filter(p -> p instanceof FieldInsnNode).findFirst().ifPresent(ain -> {
			Bootstrap.getBuilder().addField(classNode.name, ((FieldInsnNode)ain).name).putName("OtterUpdater", "id");
		});

	}
}
