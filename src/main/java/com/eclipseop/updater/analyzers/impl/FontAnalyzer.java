package com.eclipseop.updater.analyzers.impl;

import com.eclipseop.updater.Bootstrap;
import com.eclipseop.updater.analyzers.Analyzer;
import org.objectweb.asm.tree.ClassNode;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * Created by Eclipseop.
 * Date: 8/1/2017.
 */
public class FontAnalyzer extends Analyzer {
	@Override
	public ClassNode findClassNode(ArrayList<ClassNode> classNodes) {
		final ClassNode[] classNode = new ClassNode[1];

		classNodes.stream()
				.filter(p -> Modifier.isFinal(p.access)).
				forEach(c -> {
					c.methods.stream()
							.filter(p -> p.name.equals("<init>"))
							.filter(p -> p.desc.equals("([B[I[I[I[I[I[[B)V"))
							.forEach(methodNode -> {
								classNode[0] = c;
								Bootstrap.getBuilder().addClass(c.name).putName("OtterUpdater", "Font");
							});
				});

		return classNode[0];
	}

	@Override
	public void findHooks(ClassNode classNode) {

	}
}
