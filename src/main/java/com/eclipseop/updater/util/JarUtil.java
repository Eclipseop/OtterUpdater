package com.eclipseop.updater.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.jar.JarFile;

/**
 * Created by Eclipseop.
 * Date: 6/30/2017.
 */
public class JarUtil {

	public static ArrayList<ClassNode> parseJar(final JarFile jar) {
		final ArrayList<ClassNode> tempList = new ArrayList<>();

		try {
			jar.stream()
					.filter(p -> p.getName().endsWith(".class"))
					.forEach(c -> {
						try {
							final ClassReader classReader = new ClassReader(jar.getInputStream(c));
							final ClassNode classNode = new ClassNode();

							classReader.accept(classNode, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
							tempList.add(classNode);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});

			jar.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tempList;
	}
}
