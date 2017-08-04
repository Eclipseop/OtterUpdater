package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.Mask;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eclipseop.
 * Date: 7/22/2017.
 */
public class PlayerAnalyzer extends Analyzer {
	/*
	@Override
	public ClassNode identifyClass(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findClass("Actor").getClassObsName()))
				.filter(p -> p.fields.size() > 5)
				.forEach(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "name", "actions").putName("OtterUpdater", "Player");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.fields.stream().filter(p -> !Modifier.isStatic(p.access)).forEach(field -> {
			if (field.desc.equals("Ljava/lang/String;")) {
				Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "name");
			} else if (field.desc.equals("[Ljava/lang/String;")) {
				Bootstrap.getBuilder().addField(classNode.name, field.name).putName("OtterUpdater", "actions");
			}
		});
	}
	*/
	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (Modifier.isFinal(classNode.access) && classNode.superName.equals(FoundUtil.findClass("Actor").getRef().name)) {
				return new FoundClass(classNode, "Player").addExpectedField("name", "actions", "combatLevel", "totalLevel");
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode fieldNode : foundClass.getRef().fields) {
			if (!Modifier.isStatic(fieldNode.access)) {
				if (fieldNode.desc.equals("Ljava/lang/String;")) {
					foundClass.addFields(new FoundField(fieldNode, "name"));
				} else if (fieldNode.desc.equals("[Ljava/lang/String;")) {
					foundClass.addFields(new FoundField(fieldNode, "actions"));
				}
			}
		}

		boolean combatFound = false;
		boolean totalFound = false;
		level:
		for (MethodNode methodNode : foundClass.getRef().methods) {
			if (methodNode.access != Opcodes.ACC_FINAL || !methodNode.desc.startsWith("(" + FoundUtil.findClass("Buffer").getRef().getWrappedName())) {
				continue;
			}

			final List<List<AbstractInsnNode>> all = Mask.findAll(
					methodNode,
					Mask.BIPUSH,
					Mask.INVOKEVIRTUAL,
					Mask.PUTFIELD.describe("I")
			);
			if (all == null) {
				continue;
			}

			int operand = -1;
			for (List<AbstractInsnNode> abstractInsnNodes : all) {
				for (AbstractInsnNode ain : abstractInsnNodes) {
					if (ain instanceof IntInsnNode) {
						operand = ((IntInsnNode) ain).operand;
						continue;
					}

					if (ain instanceof FieldInsnNode) {
						String hookName = null;
						if (operand == -109 && !totalFound) {
							hookName = "totalLevel";
							totalFound = true;
						} else if (operand == -113 && !combatFound) {
							hookName = "combatLevel";
							combatFound = true;
						} else {
							continue;
						}

						foundClass.addFields(new FoundField(foundClass.findField((FieldInsnNode) ain), hookName));

						if (combatFound && totalFound) {
							break level;
						}
					}
				}
			}
		}
	}
}
