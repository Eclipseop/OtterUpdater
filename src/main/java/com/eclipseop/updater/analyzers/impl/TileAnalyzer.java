package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.Mask;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eclipseop.
 * Date: 7/2/2017.
 */
public class TileAnalyzer extends Analyzer {

	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];
		classNodes.stream()
				.filter(p -> Modifier.isFinal(p.access))
				.filter(p -> p.superName.equals(Bootstrap.getBuilder().findByName("Node").getClassObsName()))
				.filter(p -> p.methods.stream().filter(m -> m.name.equals("<init>")).findFirst().orElse(null).desc.contains("III"))
				.findFirst()
				.ifPresent(c -> {
					classNode[0] = c;
					Bootstrap.getBuilder().addClass(c.name, "pickableDecor", "strictX", "strictY", "plane").putName("OtterUpdater", "Tile");
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.fields.stream()
				.filter(p -> p.desc.equals("L" + Bootstrap.getBuilder().findByName("PickableDecor").getClassObsName() + ";"))
				.findFirst()
				.ifPresent(c -> {
					Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "pickableDecor");
				});

		classNode.methods.stream()
				.filter(p -> p.name.equals("<init>"))
				.findFirst()
				.ifPresent(method -> {
					final List<List<AbstractInsnNode>> all = Mask.findAll(method, Mask.ILOAD, Mask.PUTFIELD.own(classNode.name).distance(3));

					final boolean[] xFound = {false};
					final boolean[] yFound = {false};

					for (List<AbstractInsnNode> abstractInsnNodes : all) {
						final int[] index = {-1};

						abstractInsnNodes.forEach(c -> {
							if (c instanceof VarInsnNode) {
								index[0] = ((VarInsnNode) c).var;
							}

							if (c instanceof FieldInsnNode) {
								if (index[0] == 2 && !xFound[0]) {
									xFound[0] = true;
									Bootstrap.getBuilder().addField(classNode.name, ((FieldInsnNode) c).name).putName("OtterUpdater", "strictX");
								} else if (index[0] == 3 && !yFound[0]) {
									yFound[0] = true;
									Bootstrap.getBuilder().addField(classNode.name, ((FieldInsnNode) c).name).putName("OtterUpdater", "strictY");
								}
							}
						});
					}

					final List<AbstractInsnNode> abstractInsnNodes = Mask.find(method, Mask.DUP_X1, Mask.PUTFIELD.distance(2));
					for (AbstractInsnNode abstractInsnNode : abstractInsnNodes) {
						if (abstractInsnNode instanceof FieldInsnNode) {
							Bootstrap.getBuilder().addField(classNode.name, ((FieldInsnNode) abstractInsnNode).name).putName("OtterUpdater", "plane");
						}
					}
				});
	}
}
