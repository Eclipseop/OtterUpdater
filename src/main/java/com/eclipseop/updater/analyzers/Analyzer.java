package com.eclipseop.updater.analyzers;

import com.eclipseop.updater.util.found_shit.FoundClass;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 6/30/2017.
 */
public abstract class Analyzer {

	public abstract FoundClass identifyClass(final ArrayList<ClassNode> classNodes);

	public abstract void findHooks(final FoundClass foundClass);

	private static ArrayList<ClassNode> classNodes;

	public static void setClassNodes(final ArrayList<ClassNode> list) {
		classNodes = list;
	}

	public ArrayList<ClassNode> getClassNodes() {
		return classNodes;
	}

	public void run(){
		final FoundClass foundClass = identifyClass(classNodes);
		if (foundClass == null || foundClass.getRef() == null) {
			System.out.println("Failed to find " + getClass().getSimpleName());
			return;
		}

		FoundUtil.addClass(foundClass);

		findHooks(foundClass);
	}
}
