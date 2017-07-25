package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.Mask;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eclipseop.
 * Date: 7/24/2017.
 */
public class InterfaceComponentAnalyzer extends Analyzer {
	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findByName("Node").getClassObsName()))
				.filter(p -> p.fieldCount("[Ljava/lang/Object;", true) > 10)
				.findFirst()
				.ifPresent(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "components", "parent", "text").putName("OtterUpdater", "InterfaceComponent");
				});


		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.fields.stream().forEach(field -> {
			if (field.desc.equals("[L" + classNode.name + ";")) {
				Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "components");
			} else if (field.desc.equals("L" + classNode.name + ";")) {
				Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "parent");
			}
		});

		final List<AbstractInsnNode> abstractInsnNodes = Mask.find(classNode, Mask.INVOKEVIRTUAL, Mask.PUTFIELD.own(classNode.name));
		abstractInsnNodes.stream().filter(p -> p instanceof FieldInsnNode).findFirst().ifPresent(ain -> {
			Bootstrap.getBuilder().addField(classNode.name, ((FieldInsnNode)ain).name).putName("OtterUpdater", "text");
		});

		/*
		getClassNodes().forEach(node -> {
			node.methods.stream()
					.filter(p -> p.desc.contains("L" + Bootstrap.getBuilder().findByName("ScriptEvent").getClassObsName() + ";") ||
							p.desc.contains("L" + Bootstrap.getBuilder().findByName("RuneScript").getClassObsName() + ";"))
					.forEach(method -> {
						Arrays.stream(method.instructions.toArray()).filter(p -> p instanceof IntInsnNode).forEach(inst -> {
							final IntInsnNode iin = (IntInsnNode) inst;
							switch (iin.operand) {
								case 1109:
									System.out.println(node.name + "." + method.name + "()");
									final AbstractInsnNode next = Mask.next(iin, 100, Mask.PUTFIELD.own(classNode.name).distance(10));
									if (next instanceof FieldInsnNode) {
										System.out.println(((FieldInsnNode) next).owner + "." + ((FieldInsnNode) next).name);
									}
							}
						});
					});
		});
		*/
	}
}
