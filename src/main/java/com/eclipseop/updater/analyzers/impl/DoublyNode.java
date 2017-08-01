package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Eclipseop.
 * Date: 7/1/2017.
 */
public class DoublyNode extends Analyzer {

	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findByName("Node").getClassObsName()))
				.filter(p -> p.fieldCount("L" + p.name + ";", true) == 2)
				.forEach(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "previous", "next").putName("OtterUpdater", "DoublyNode");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		final String[] temp = {null};

		Arrays.stream(classNode.methods.stream().filter(p -> !Modifier.isStatic(p.access)).findFirst().get().instructions.toArray())
				.filter(p -> p instanceof FieldInsnNode)
				.forEach(c -> {
					final FieldInsnNode node = (FieldInsnNode) c;
					if (temp[0] == null) {
						Bootstrap.getBuilder().addField(classNode.name, node.name).putName("OtterUpdater", "previous");
						temp[0] = node.name;
					} else if (!temp[0].equals(((FieldInsnNode) c).name)) {
						Bootstrap.getBuilder().addField(classNode.name, node.name).putName("OtterUpdater", "next");
					}
				});
	}
}
