package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.Mask;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Eclipseop.
 * Date: 7/2/2017.
 */
public class ActorAnalyzer extends Analyzer {

	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findByName("Entity").getClassObsName()))
				.filter(p -> Modifier.isAbstract(p.access))
				.forEach(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "strictX", "strictY", "hitsplatCount", "animation").putName("OtterUpdater", "Actor");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.fields.stream().filter(p -> !Modifier.isStatic(p.access)).forEach(c -> {
			if (c.desc.equals("[B")) {
				Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "hitsplatCount");
			}
		}); //hitsplat

		getClassNodes().stream().forEach(node -> {
			node.methods.stream()
					.filter(p -> p.desc.startsWith("(L" + classNode.name + ";I") && p.desc.endsWith(")V"))
					.filter(p -> Modifier.isFinal(p.access) && Modifier.isStatic(p.access))
					.filter(p -> Arrays.stream(p.instructions.toArray()).filter(p1 -> p1 instanceof FieldInsnNode).count() == 2)
					.findFirst()
					.ifPresent(c -> {
						String name = null;
						for (AbstractInsnNode abstractInsnNode : c.instructions.toArray()) {
							if (abstractInsnNode instanceof FieldInsnNode) {
								if (name == null) {
									name = "strictX";
								} else {
									name = "strictY";
								}

								Bootstrap.getBuilder().addField(classNode.name, ((FieldInsnNode) abstractInsnNode).name).putName("OtterUpdater", name);
							}
						}
					});
		}); //x/y

		getClassNodes().forEach(node -> {
			node.methods.stream()
					.filter(p -> p.desc.contains(Bootstrap.getBuilder().findByName("Actor").getClassObsName()))
					.findFirst()
					.ifPresent(method -> {
						final List<AbstractInsnNode> i =
								Mask.find(
										method,
										Mask.GETFIELD.describe("I"),
										Mask.LDC.distance(1),
										Mask.INVOKESTATIC.describe("L" + Bootstrap.getBuilder().findByName("AnimationSequence").getClassObsName() + ";", true).distance(5)
								);
						if (i != null) {
							i.stream().filter(p -> p instanceof FieldInsnNode).findFirst().ifPresent(c -> {
								Bootstrap.getBuilder().addField(classNode.name, ((FieldInsnNode) c).name).putName("OtterUpdater", "animation");
							});
						}
					});


		}); //animation
	}
}
