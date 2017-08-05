package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundField;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 8/1/2017.
 */
public class GrandExchangeOffer extends Analyzer {

	@Override
	public FoundClass identifyClass(ArrayList<ClassNode> classNodes) {
		for (ClassNode classNode : classNodes) {
			if (classNode.fieldCount("B", true) == 1 && classNode.fieldCount("I", true) == 5) {
				return new FoundClass(classNode, "GrandExchangeOffer")
						.addExpectedField("metadata", "itemId", "itemPrice", "itemQuantity", "amountTransferred", "amountSpent");
			}
		}

		return null;
	}

	@Override
	public void findHooks(FoundClass foundClass) {

		int found = 0;
		hooks:
		for (MethodNode methodNode : foundClass.getRef().methods) {
			if (!methodNode.name.equals("<init>")) {
				continue;
			}

			AbstractInsnNode ain = methodNode.instructions.getFirst();
			while ((ain = ain.getNext()) != null) {
				if (found >= 6) {
					break hooks;
				}

				if (ain.opcode() == Opcodes.GOTO) {
					ain = ((JumpInsnNode) ain).label.getNext();
				} else if (ain instanceof FieldInsnNode) {
					found++;

					String name = null;
					if (found == 1) {
						name = "metadata";
					} else if (found == 2) {
						name = "itemId";
					} else if (found == 3) {
						name = "itemPrice";
					} else if (found == 4) {
						name = "itemQuantity";
					} else if (found == 5) {
						name = "amountTransferred";
					} else if (found == 6) {
						name = "amountSpent";
					}

					foundClass.addFields(new FoundField(foundClass.findField((FieldInsnNode) ain), name));
				}
			}
		}
	}
}
