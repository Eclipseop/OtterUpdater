package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 7/24/2017.
 */
public class PickableNodeAnalyzer extends Analyzer { //the actual item
	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findByName("Entity").getClassObsName()))
				.filter(p -> Modifier.isFinal(p.access))
				.filter(p -> p.fieldCount("I", true) == 2)
				.findFirst()
				.ifPresent(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "id", "stackSize").putName("OtterUpdater", "PickableNode");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.methods.stream()
				.filter(p -> !Modifier.isStatic(p.access))
				.filter(p -> !p.name.equals("<init>"))
				.findFirst()
				.ifPresent(method -> {
					String name = null;
					for (AbstractInsnNode abstractInsnNode : method.instructions.toArray()) {
						if (abstractInsnNode instanceof FieldInsnNode) {
							if (name == null) {
								name = "id";
							} else {
								name = "stackSize";
							}

							Bootstrap.getBuilder().addField(classNode.name, ((FieldInsnNode) abstractInsnNode).name).putName("OtterUpdater", name);
						}
					}
				});
	}
}
