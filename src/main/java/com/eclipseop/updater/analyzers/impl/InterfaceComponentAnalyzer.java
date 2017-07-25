package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

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
					Bootstrap.getBuilder().addClass(c.name, "components", "parent").putName("OtterUpdater", "InterfaceComponent");
				});


		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		/*
		classNode.fields.stream().filter(p -> p.desc.equals("[L" + classNode.name + ";")).findFirst().ifPresent(field -> {
			Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "components");
		});
		*/

		classNode.fields.stream().forEach(field -> {
			if (field.desc.equals("[L" + classNode.name + ";")) {
				Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "components");
			} else if (field.desc.equals("L" + classNode.name + ";")) {
				Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "parent");
				System.out.println("1");
			}
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
