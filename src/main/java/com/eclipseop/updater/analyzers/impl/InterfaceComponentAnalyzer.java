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
				.forEach(c -> {
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

		final List<AbstractInsnNode> textMask = Mask.find(classNode, Mask.INVOKEVIRTUAL, Mask.PUTFIELD.own(classNode.name).describe("Ljava/lang/String;"));
		if (textMask != null) {
			textMask.stream().filter(p -> p instanceof FieldInsnNode).findFirst().ifPresent(ain -> {
				Bootstrap.getBuilder().addField(classNode.name, ((FieldInsnNode) ain).name).putName("OtterUpdater", "text");
			});
		}
	}
}
