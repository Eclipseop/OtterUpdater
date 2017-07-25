package com.eclipseop.updater.analyzers;

import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 6/30/2017.
 */
public abstract class Analyzer {

	public abstract ClassNode findClassNode(final ArrayList<ClassNode> classNodes);

	public abstract void findHooks(final ClassNode classNode);

	private static ArrayList<ClassNode> classNodes;

	public static void setClassNodes(final ArrayList<ClassNode> list) {
		classNodes = list;
	}

	public ArrayList<ClassNode> getClassNodes() {
		return classNodes;
	}

	public void run(){
		final ClassNode found = findClassNode(classNodes);
		if (found == null) {
			System.out.println("Failed to find " + getClass().getSimpleName());
			return;
		}

		findHooks(found);
	}
}
