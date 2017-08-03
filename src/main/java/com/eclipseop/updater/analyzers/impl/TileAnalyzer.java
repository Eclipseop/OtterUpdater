package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import com.eclipseop.updater.util.Mask;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eclipseop.
 * Date: 7/2/2017.
 */
public class TileAnalyzer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (Modifier.isFinal(classNode.access) && classNode.superName.equals(FoundUtil.findClass("Node").getRef().name)) {
				for (MethodNode method : classNode.methods) {
					if (method.name.equals("<init>") && method.desc.equals("(III)V")) {
						return new FoundClass(classNode, "Tile").addExpectedField("pickableDecor", "strictX", "strictY", "plane");
					}
				}
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {
		for (FieldNode field : foundClass.getRef().fields) {
			if (field.desc.equals(FoundUtil.findClass("PickableDecor").getRef().getWrappedName())) {
				foundClass.addFields(new FoundField(field, "pickableDecor"));
			}
		}

		for (MethodNode method : foundClass.getRef().methods) {
			if (!method.name.equals("<init>")) {
				continue;
			}

			final List<List<AbstractInsnNode>> positionMask = Mask.findAll(method, Mask.ILOAD, Mask.PUTFIELD.own(foundClass.getRef().name));
			boolean xFound = false;
			boolean yFound = false;
			for (List<AbstractInsnNode> abstractInsnNodes : positionMask) {

				int index = -1;
				for (AbstractInsnNode ain : abstractInsnNodes) {
					if (ain instanceof VarInsnNode) {
						index = ((VarInsnNode) ain).var;
					} else if (ain instanceof FieldInsnNode) {
						if (index == 2 && !xFound) {
							foundClass.addFields(new FoundField(foundClass.findField((FieldInsnNode) ain), "strictX"));
							xFound = true;
						} else if (index == 3 && !yFound) {
							foundClass.addFields(new FoundField(foundClass.findField((FieldInsnNode) ain), "strictY"));
							yFound = true;
						}
					}
				}
			}

			final List<AbstractInsnNode> planeMask = Mask.find(method, Mask.DUP_X1, Mask.PUTFIELD);
			for (AbstractInsnNode ain : planeMask) {
				if (ain instanceof FieldInsnNode) {
					foundClass.addFields(new FoundField(foundClass.findField((FieldInsnNode) ain), "plane"));
				}
			}

			break;
		}
	}
}
