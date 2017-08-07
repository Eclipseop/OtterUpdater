package com.eclipseop.updater.deob;

import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created by Eclipseop.
 * Date: 8/6/2017.
 */
public class Deobfuscation {

	public static ClassNode test(final ClassNode classNodeToDeob) {
		final FoundClass foundClass = FoundUtil.findClass(classNodeToDeob);

		final ClassNode classNode = new ClassNode();

		classNode.name = foundClass.getName();
		for (FoundField foundField : foundClass.getFields()) {
			classNode.fields.add(
					new FieldNode(
							classNode,
							foundField.getRef().access,
							foundField.getName(),
							replaceDesc(foundField.getRef().desc),
							foundField.getRef().signature,
							null
					)
			);
		}

		for (MethodNode method : foundClass.getRef().methods) {
			final MethodNode newMethod = new MethodNode(
					classNode, method.access, method.name, method.desc, method.signature, null
			);

			newMethod.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "cm", "gc", "J"));

			classNode.methods.add(newMethod);
		}



		return classNode;
	}

	private static String replaceDesc(String desc) {
		desc = desc.replaceAll("L", "").replaceAll(";", "");

		for (FoundClass foundClass : FoundUtil.getFoundClasses()) {
			if (foundClass.getRef().name.equals(desc)) {
				return foundClass.getName();
			}
		}
		return desc;
	}
}
