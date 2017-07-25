package com.eclipseop.updater.util;

import java.util.*;

/**
 * Created by Eclipseop.
 * Date: 7/1/2017.
 */
public class ResultsBuilder {

	private Map<String, Object> metadata = new HashMap<>();
	private Map<String, MappedEntity> classMap = new HashMap<>();
	private Map<String, MappedEntity> fieldMap = new HashMap<>();
	private Map<String, MappedEntity> methodMap = new HashMap<>();
	private Map<String, ArrayList<String>> expectedMap = new HashMap<>();

	public MappedEntity addClass(String classObsName, String... expectedFields) {
		String key = classObsName;
		expectedMap.computeIfAbsent(classObsName, k -> new ArrayList<>()).addAll(Arrays.asList(expectedFields));
		return classMap.computeIfAbsent(key, k -> new MappedEntity()).putProperty("classObsName", classObsName);
	}

	public MappedEntity addMethod(String classObsName, String methodObsName) {
		String key = classObsName + "." + methodObsName;
		return methodMap.computeIfAbsent(key, s -> new MappedEntity())
				.putProperty("classObsName", classObsName)
				.putProperty("entityObsName", methodObsName);
	}

	public MappedEntity addField(String classObsName, String fieldObsName) {
		String key = classObsName + "." + fieldObsName;
		return fieldMap.computeIfAbsent(key, s -> new MappedEntity())
				.putProperty("classObsName", classObsName)
				.putProperty("entityObsName", fieldObsName);
	}

	public Map<String, MappedEntity> getClassMap() {
		return classMap;
	}

	public Map<String, MappedEntity> getFieldMap() {
		return fieldMap;
	}

	public Map<String, MappedEntity> getMethodMap() {
		return methodMap;
	}

	public ResultsBuilder print() {
		build();
		metadata.entrySet().forEach(System.out::print);
		System.out.println();
		getClassMap().values().forEach(System.out::println);
		System.out.println();
		getFieldMap().values().forEach(System.out::println);
		System.out.println();
		getMethodMap().values().forEach(System.out::println);
		return this;
	}

	public ResultsBuilder prettyPrint() {
		build();

		final List<MappedEntity> objects = new ArrayList<>(classMap.values()); //anme of our name
		objects.sort(Comparator.comparing(o -> ((String) o.getName("OtterUpdater").toArray()[0])));

		for (MappedEntity object : objects) {
			System.out.println("⌂ " + object.getName("OtterUpdater").toArray()[0] + ": " + object.getClassObsName());

			fieldMap.values().stream()
					.filter(p -> p.getProperties().get("classObsName").equals(object.getClassObsName()))
					.forEach(c -> {
						String line =
								"੦ " + c.getName("OtterUpdater").toArray()[0] + " - " +
										(c.isStatic() ? c.getStaticHost() : c.getClassObsName()) + "." + c.getEntityObsName();
						if (c.getMultiplier() != null) {
							line += " * " + c.getMultiplier();
						}
						System.out.println(line);
					});

			//broke
			expectedMap.forEach((c, f) -> { //c classnode name, f arraylist of expected fields
				if (object.getClassObsName().equals(c)) {

					for (String expected : f) {
						if (!fieldMap.values().stream().filter(p -> p.getClassObsName().equals(c)).anyMatch(p -> p.getName("OtterUpdater").toArray()[0].equals(expected))) {
							System.out.println("✖ BROKE - " + expected);
						}
					}
				}
			});
			System.out.println();
		}

/*
		classMap.forEach((s, m) -> {





			System.out.println("⌂ " + m.getName("OtterUpdater").toArray()[0] + ": " + s);

			fieldMap.values().stream()
					.filter(p -> p.getProperties().get("classObsName").equals(s))
					.forEach(c -> {
						String line =
								"੦ " + c.getName("OtterUpdater").toArray()[0] + " - " +
										(c.isStatic() ? c.getStaticHost() : c.getClassObsName()) + "." + c.getEntityObsName();
						if (c.getMultiplier() != null) {
							line += " * " + c.getMultiplier();
						}
						System.out.println(line);
					});

			//broke
			expectedMap.forEach((c, f) -> { //c classnode name, f arraylist of expected fields
				if (m.getClassObsName().equals(c)) {

					for (String expected : f) {
						if (!fieldMap.values().stream().filter(p -> p.getClassObsName().equals(c)).anyMatch(p -> p.getName("OtterUpdater").toArray()[0].equals(expected))) {
							System.out.println("✖ BROKE - " + expected);
						}
					}
				}
			});
			System.out.println();
		});
		*/
		return this;
	}

	public void build() {
		metadata.put("classesFound", classMap.size());
		metadata.put("fieldsFound", fieldMap.size());
		metadata.put("methodsFound", methodMap.size());

		for (MappedEntity mappedEntity : fieldMap.values()) {
			Optional.ofNullable(getClassMap().get(mappedEntity.isStatic() ? "client" : mappedEntity.getClassObsName())).ifPresent(mappedClass -> {
				mappedEntity.putProperty("classNames", mappedClass.getNames());
			});
		}

		for (MappedEntity mappedEntity : methodMap.values()) {
			Optional.ofNullable(getClassMap().get(mappedEntity.isStatic() ? "client" : mappedEntity.getClassObsName())).ifPresent(mappedClass -> {
				mappedEntity.putProperty("classNames", mappedClass.getNames());
			});
		}
	}

	public MappedEntity findByName(final String name) {
		return getClassMap().values().stream()
				.filter(p -> p.getName("OtterUpdater").contains(name))
				.findFirst().get();
	}

	public static class MappedEntity {

		private Map<String, HashSet<String>> names = new HashMap<>();
		private Map<String, Object> properties = new HashMap<>();

		public MappedEntity putProperty(String key, Object value) {
			if (value == null) return this;
			properties.put(key, value);
			return this;
		}

		public MappedEntity putTypeObs(String type) {
			return putProperty("typeObs", type);
		}

		public MappedEntity putJunkParam(String type) {
			return putProperty("junkParam", type);
		}

		/*
		public MappedEntity putStatic(boolean isStatic) {
			return putProperty("isStatic", isStatic);
		}
		*/

		public MappedEntity putStatic(final String host) {
			return putProperty("staticHost", host);
		}

		public MappedEntity putMultiplier(String multi) {
			return putProperty("multiplier", multi);
		}

		public Map<String, Object> getProperties() {
			return properties;
		}

		public Map<String, HashSet<String>> getNames() {
			return names;
		}

		public MappedEntity putName(String updater, String name) {
			names.computeIfAbsent(updater, s -> new HashSet<>()).add(name);
			return this;
		}

		@Override
		public String toString() {
			return "MappedEntity{" +
					"names=" + names +
					", properties=" + properties +
					'}';
		}

		public void putNames(String updater, HashSet<String> names) {
			for (String name : names) {
				putName(updater, name);
			}
		}

		public String getClassObsName() {
			return (String) getProperties().get("classObsName");
		}

		public HashSet<String> getName(String updater) {
			return getNames().get(updater);
		}

		public String getEntityObsName() {
			return (String) getProperties().get("entityObsName");
		}

		public boolean isStatic() {
			return getStaticHost() != null;
		}

		public String getStaticHost() {
			return (String) properties.getOrDefault("staticHost", null);
		}

		public String getTypeObs() {
			return (String) properties.getOrDefault("typeObs", null);
		}

		public String getMultiplier() {
			String multiplier = (String) properties.getOrDefault("multiplier", null);
			if (multiplier == null) return null;
			return multiplier;
		}

		public String getJunkParam() {
			String multiplier = (String) properties.getOrDefault("junkParam", null);
			if (multiplier == null) return null;
			return multiplier;
		}
	}

	public static void main(String[] args) {
		final Map<String, String> stringStringTreeMap = new TreeMap<>();
		stringStringTreeMap.put("x", "x");
		stringStringTreeMap.put("a", "a");
		stringStringTreeMap.put("d", "d");
		for (String s : stringStringTreeMap.keySet()) {
			System.out.println(s);
		}
	}
}
