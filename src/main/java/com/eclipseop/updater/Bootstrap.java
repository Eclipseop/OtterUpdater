package com.eclipseop.updater;

import com.eclipseop.updater.analyzers.Analyzer;
import com.eclipseop.updater.analyzers.impl.*;
import com.eclipseop.updater.util.found_shit.FoundUtil;
import com.eclipseop.updater.util.JarUtil;
import com.eclipseop.updater.util.ast.AbstractSyntaxTree;
import com.eclipseop.updater.util.ast.expression.Expression;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;

/**
 * Created by Eclipseop.
 * Date: 6/30/2017.
 */
public class Bootstrap {

	private static final ArrayList<Analyzer> analyzers = new ArrayList<>();
	private static ArrayList<ClassNode> classNodes;

	static {
		try {
			classNodes = JarUtil.parseJar(new JarFile("C:\\Users\\eclip\\Desktop\\pepes\\gamepack\\150.jar"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<ClassNode> getClassNodes() {
		return classNodes;
	}

	private static void loadAnalyzers() {
		analyzers.add(new NodeAnalyzer());
		analyzers.add(new DoublyNodeAnalyzer());
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
		analyzers.add(new FontAnalyzer());
		analyzers.add(new BufferAnalyzer());
		analyzers.add(new GrandExchangeOffer());
		analyzers.add(new MouseRecorderAnalyzer());
		analyzers.add(new ClientAnalyzer());
	}

	/*
	private static void findMultipliers(final List<Expression> list) {
		final HashMap<String, ArrayList<Integer>> dankMap = new HashMap<>();

		for (Expression expression : list) {
			if (expression instanceof MathExpression) {
				final MathExpression me = (MathExpression) expression;

				if (me.getOperator().equals("*")) {
					String field = null;
					int multi = -1;

					if (me.getLeft() instanceof InstanceExpression) {
						field = ((InstanceExpression) me.getLeft()).getFieldName();
					}

					if (me.getRight() instanceof InstanceExpression) {
						field = ((InstanceExpression) me.getRight()).getFieldName();
					}

					if (me.getLeft() instanceof VarExpression) {
						final VarExpression left = (VarExpression) me.getLeft();
						if (left.getVarName().contains(".")) {
							field = left.getVarName();
						} else if (isInteger(left.getVarName())) {
							multi = Integer.parseInt(left.getVarName());
						}
					}

					if (me.getRight() instanceof VarExpression) {
						final VarExpression right = (VarExpression) me.getRight();
						if (right.getVarName().contains(".")) {
							field = right.getVarName();
						} else if (isInteger(right.getVarName())) {
							multi = Integer.parseInt(right.getVarName());
						}
					}

					if (field != null && multi != -1) {
						dankMap.computeIfAbsent(field, f -> new ArrayList<>()).add(multi);
					}
				}
			}
		}

		dankMap.keySet().forEach(c -> {
			if (c.equals("bi.k")) {

			}

			//final ResultsBuilder.MappedEntity mappedEntity = builder.getFieldMap().get(c);
			final ResultsBuilder.MappedEntity mappedEntity = builder.findField(c);

			if (mappedEntity != null) {
				mappedEntity.putMultiplier(String.valueOf(mostCommon(dankMap.get(c))));
			}
		});
	}

	private static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
		} catch (Exception e) {
			return false;
		}
		return true;
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
	*/

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
			System.out.println("Running on revision " + getRevision(classNodes));

			final long analyzerTime = System.currentTimeMillis();
			loadAnalyzers();
			Analyzer.setClassNodes(classNodes);
			analyzers.forEach(Analyzer::run);
			System.out.println("Identifying hooks... " + ((double) (System.currentTimeMillis() - analyzerTime) / 1000) + "s");

			final long multiTime = System.currentTimeMillis();
			final List<Expression> ast = AbstractSyntaxTree.find(classNodes, Opcodes.IMUL);
			//findMultipliers(ast);
			System.out.println("Collecting multipliers... " + ((double) (System.currentTimeMillis() - multiTime) / 1000) + "s");

			System.out.println();

			FoundUtil.print();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
