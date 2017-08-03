package com.eclipseop.updater.util.found_shit;

import org.objectweb.asm.tree.FieldNode;

/**
 * Created by Eclipseop.
 * Date: 8/1/2017.
 */
public class FoundField {

	private final FieldNode ref;
	private final String name;

	private int multiplier;

	public FoundField(FieldNode ref, String name) {
		this.ref = ref;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public FieldNode getRef() {
		return ref;
	}

	public FoundField setMultiplier(final int multiplier) {
		this.multiplier = multiplier;
		return this;
	}
}
