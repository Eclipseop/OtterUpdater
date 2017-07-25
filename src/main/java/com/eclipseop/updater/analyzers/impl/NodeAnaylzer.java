package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 6/30/2017.
 */
public class NodeAnaylzer extends Analyzer {

	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] node = new ClassNode[1];
		classNodes.stream().forEach(c -> {
			if (c.fieldCount("J", true) == 1 && c.fieldCount("L" + c.name + ";") == 2) {
				node[0] = c;
				Bootstrap.getBuilder().addClass(c.name, "key", "next", "previous").putName("OtterUpdater", "Node");
			}
		});
		return node[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {
		classNode.fields.stream().filter(p -> !Modifier.isStatic(p.access)).forEach(c -> {
			if (c.desc.equals("J")) {
				Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "key");
			}

			if (c.desc.contains(classNode.name)) {
				if (Modifier.isPublic(c.access)) {
					Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "next");
				} else {
					Bootstrap.getBuilder().addField(classNode.name, c.name).putName("OtterUpdater", "previous");
				}
			}
		});
	}
}
