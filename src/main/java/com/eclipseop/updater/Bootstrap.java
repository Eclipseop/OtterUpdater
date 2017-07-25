package com.eclipseop.updater;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.analyzers.impl.*;
import com.eclipseop.updater.util.JarUtil;
import com.eclipseop.updater.util.Mask;
import com.eclipseop.updater.util.ResultsBuilder;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;

/**
 * Created by Eclipseop.
 * Date: 6/30/2017.
 */
public class Bootstrap {

	private static final ArrayList<Analyzer> analyzers = new ArrayList<>();
	private static final ResultsBuilder builder = new ResultsBuilder();

	public static ResultsBuilder getBuilder() {
		return builder;
	}

	private static void loadAnalyzers() {
		analyzers.add(new NodeAnaylzer());
		analyzers.add(new DoublyNode());
		analyzers.add(new EntityAnalyzer());
		analyzers.add(new PickableDecorAnalyzer());
		analyzers.add(new PickableNodeAnalyzer());
		analyzers.add(new TileAnalyzer());
		analyzers.add(new SceneGraphAnalyzer());
		analyzers.add(new AnimationSequenceAnalyzer());
		analyzers.add(new ActorAnalyzer());
		analyzers.add(new GameEngineAnalyzer());
		analyzers.add(new PlayerAnalyzer());
		analyzers.add(new NpcDefinitionAnalyzer());
		analyzers.add(new NpcAnalyzer());
		analyzers.add(new ClanMateAnalyzer());
		analyzers.add(new RuneScriptAnalyzer());
		analyzers.add(new ScriptEventAnalyzer());
		analyzers.add(new InterfaceComponentAnalyzer());
		analyzers.add(new ClientAnalyzer());
	}

	private static void findMultipliers(final ArrayList<ClassNode> list) {
		final HashMap<String, ArrayList<Integer>> dankMap = new HashMap<>();

		list.forEach(classNode -> {
			final List<List<AbstractInsnNode>> all = Mask.findAll(classNode, Mask.GETFIELD, Mask.LDC.distance(3), Mask.IMUL.distance(3));

			if (all != null) {
				for (List<AbstractInsnNode> abstractInsnNodes : all) {
					int multi = -1;
					FieldInsnNode field = null;
					for (AbstractInsnNode abstractInsnNode : abstractInsnNodes) {
						if (abstractInsnNode instanceof FieldInsnNode) {
							final FieldInsnNode fin = (FieldInsnNode) abstractInsnNode;
							if (fin.desc.equals("I")) {
								field = fin;
							}
						} else if (abstractInsnNode instanceof LdcInsnNode) {
							final Object cst = ((LdcInsnNode) abstractInsnNode).cst;
							if (cst instanceof Integer) {
								multi = (int) cst;
							}
						}
					}

					if (field != null && multi != 0) {
						dankMap.computeIfAbsent(field.owner + "." + field.name, f -> new ArrayList<>()).add(multi);
					}
				}
			}
		});

		dankMap.keySet().forEach(c -> {
			final ResultsBuilder.MappedEntity mappedEntity = builder.getFieldMap().get(c);

			if (mappedEntity != null) {
				mappedEntity.putMultiplier(String.valueOf(mostCommon(dankMap.get(c))));
			}
		});
	}

	private static <T> T mostCommon(List<T> list) {
		Map<T, Integer> map = new HashMap<>();

		for (T t : list) {
			Integer val = map.get(t);
			map.put(t, val == null ? 1 : val + 1);
		}

		Map.Entry<T, Integer> max = null;

		for (Map.Entry<T, Integer> e : map.entrySet()) {
			if (max == null || e.getValue() > max.getValue())
				max = e;
		}

		return max.getKey();
	}

	private static int getRevision(final ArrayList<ClassNode> classNodes) {
		final int[] rev = new int[1];

		classNodes.stream().filter(p -> p.name.equals("client")).forEach(node -> {
			node.methods.stream().filter(p -> p.name.equals("init")).forEach(method -> {
				Arrays.stream(method.instructions.toArray()).filter(p -> p instanceof IntInsnNode && ((IntInsnNode) p).operand == 503).findFirst().ifPresent(inst -> {
					rev[0] = ((IntInsnNode) inst.getNext()).operand;
				});
			});
		});

		return rev[0];
	}

	public static void main(String[] args) {
		try {
			final ArrayList<ClassNode> classNodes = JarUtil.parseJar(new JarFile("C:\\Users\\eclip\\Desktop\\pepes\\gamepack\\150.jar"));

			System.out.println("Running on revision " + getRevision(classNodes));

			final long analyzerTime = System.currentTimeMillis();
			loadAnalyzers();
			Analyzer.setClassNodes(classNodes);
			analyzers.forEach(Analyzer::run);
			System.out.println("Identifying hooks... " + ((double) (System.currentTimeMillis() - analyzerTime) / 1000) + "s");

			final long multiTime = System.currentTimeMillis();
			findMultipliers(classNodes);
			System.out.println("Collecting multipliers... " + ((double) (System.currentTimeMillis() - multiTime) / 1000) + "s");

			System.out.println();

		} catch (IOException e) {
			e.printStackTrace();
		}

		builder.prettyPrint();
		//builder.print();
	}
}
