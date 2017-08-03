package com.eclipseop.updater.util.found_shit;

import com.eclipseop.updater.Bootstrap;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Eclipseop.
 * Date: 8/1/2017.
 */
public class FoundClass {

	private final ClassNode ref;

	private final List<FoundField> fields;
	private final List<FoundMethod> methods;

	private final List<String> expectedFields;
	private final String name;

	public FoundClass(final ClassNode ref, final String name) {
		this.ref = ref;

		this.fields = new ArrayList<>();
		this.methods = new ArrayList<>();

		this.expectedFields = new ArrayList<>();
		this.name = name;
	}

	public ClassNode getRef() {
		return ref;
	}

	public String getName() {
		return name;
	}

	public List<FoundField> getFields() {
		return fields;
	}

	public List<FoundMethod> getMethods() {
		return methods;
	}

	public List<String> getExpectedFields() {
		return expectedFields;
	}

	public FoundClass addExpectedField(final String... fields) {
		Collections.addAll(expectedFields, fields);
		return this;
	}

	public FoundClass addFields(final FoundField... fields) {
		Collections.addAll(this.fields, fields);
		return this;
	}

	public FieldNode findField(final FieldInsnNode field) {
		for (ClassNode classNode : Bootstrap.getClassNodes()) {
			for (FieldNode fieldNode : classNode.fields) {
				if (fieldNode.owner.name.equals(field.owner)) {
					if (fieldNode.name.equals(field.name)) {
						return fieldNode;
					}
				}
			}
		}

		return null;
	}
}
