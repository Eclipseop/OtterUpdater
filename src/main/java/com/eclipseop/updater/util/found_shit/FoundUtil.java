package com.eclipseop.updater.util.found_shit;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Eclipseop.
 * Date: 8/1/2017.
 */
public class FoundUtil {

	private static final List<FoundClass> foundClasses = new ArrayList<>();

	public static List<FoundClass> getFoundClasses() {
		return foundClasses;
	}

	public static FoundClass findClass(final String name) {
		return foundClasses.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
	}

	public static FoundClass findClass(final ClassNode classNode) {
		for (FoundClass foundClass : foundClasses) {
			if (foundClass.getRef().equals(classNode)) {
				return foundClass;
			}
		}
		return null;
	}

	public static FieldNode findField(final String fullname) {
		final ClassNode classNode = foundClasses.stream()
				.map(FoundClass::getRef)
				.filter(p -> p.name.equals(fullname.split("\\.")[0]))
				.findFirst().orElse(null);

		return classNode.fields.stream().filter(p -> p.name.equals(fullname.split("\\.")[1])).findFirst().orElse(null);
	}

	public static FoundField findHookedField(final String fullname) {
		for (FoundClass foundClass : foundClasses) {
			for (FoundField foundField : foundClass.getFields()) {
				if (foundField.getRef().name.equals(fullname.split("\\.")[1]) && foundField.getRef().owner.name.equals(fullname.split("\\.")[0])) {
					return foundField;
				}
			}
		}

		return null;
	}

	public static void addClass(final FoundClass foundClass) {
		foundClasses.add(foundClass);
	}

	private static String prettyDesc(final String desc) {
		String temp = desc;

		if (temp.endsWith(";")) {
			temp = temp.replaceAll("L", "").replaceAll(";", "");
		}

		temp = temp.replaceAll("I", "int").replaceAll("J", "long").replaceAll("B", "byte").replaceAll("Z", "boolean");

		while (temp.startsWith("[")) {
			temp = temp.substring(1) + "[]";
		}

		while (temp.contains("/")) {
			temp = temp.substring(temp.indexOf("/") + 1);
		}

		return temp;
	}

	public static void print() {
		foundClasses.sort(Comparator.comparing(FoundClass::getName));

		for (FoundClass foundClass : foundClasses) {
			System.out.println("⌂ " + foundClass.getName() + ": " + foundClass.getRef().name);
			foundClass.getFields().sort(Comparator.comparing(o -> o.getRef().desc));
			for (FoundField foundField : foundClass.getFields()) {

				System.out.println("੦ " + prettyDesc(foundField.getRef().desc) + " " + foundField.getName() + " - " + foundField.getRef().owner + "." + foundField.getRef().name
						+ (foundField.getMultiplier() != -1 ? " * " + foundField.getMultiplier() : ""));
			}

			for (String expectedField : foundClass.getExpectedFields()) {
				if (!foundClass.getFields().stream().map(FoundField::getName).anyMatch(p -> p.equals(expectedField))) {
					System.out.println("✖ BROKE - " + expectedField);
				}
			}

			System.out.println();
		}
	}
}
