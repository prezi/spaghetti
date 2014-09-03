package com.prezi.spaghetti.javascript;

import com.google.common.base.Strings;
import com.prezi.spaghetti.ast.QualifiedNode;
import com.prezi.spaghetti.ast.StringModuleVisitorBase;

import java.util.Arrays;
import java.util.List;

public class AbstractJavaScriptGeneratorVisitor extends StringModuleVisitorBase {
	protected static <T extends QualifiedNode> String embedInPackageStructure(T node, PackagedGenerator<T> generator) {
		List<String> levels = Arrays.asList(node.getQualifiedName().namespace.split("\\."));
		String topLevel = levels.get(0);
		return "var " + topLevel + ";\n"
				+ "(function (" + topLevel + ") {\n"
				+ defineLevel(node, 1, topLevel, levels.subList(1, levels.size()), generator)
				+ "})(" + topLevel + " || " + topLevel + " = {}));\n";
	}

	private static <T extends QualifiedNode> String defineLevel(T node, int indentLevel, String parent, List<String> remainingLevels, PackagedGenerator<T> generator) {
		final String indent = Strings.repeat("\t", indentLevel);
		String result;
		if (remainingLevels.isEmpty()) {
			result = generator.generate(node, indentLevel, parent);
		} else {
			String level = remainingLevels.get(0);
			result = indent + "(function(" + level + ") {\n"
						+ defineLevel(node, indentLevel + 1, level, remainingLevels.subList(1, remainingLevels.size()), generator)
					+ indent + "})(" + parent + "." + level + " || (" + parent + "." + level + " = {}));\n"
					+ indent + "var " + level + " = " + parent + "." + level + ";\n";
		}
		return result;
	}

	protected static interface PackagedGenerator<T extends QualifiedNode> {
		String generate(T node, int indentLevel, String parent);
	}
}
