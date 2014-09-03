package com.prezi.spaghetti.javascript;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.prezi.spaghetti.ast.ConstEntryNode;
import com.prezi.spaghetti.ast.ConstNode;
import com.prezi.spaghetti.ast.StringModuleVisitorBase;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.List;

public class JavaScriptConstGeneratorVisitor extends StringModuleVisitorBase {

	@Override
	public String visitConstNode(ConstNode node) {
		List<String> levels = node.getQualifiedName().getParts();
		String topLevel = levels.get(0);
		return "var " + topLevel + ";\n"
				+ "(function (" + topLevel + ") {\n"
				+ defineLevel(node, 1, topLevel, levels.subList(1, levels.size()))
				+ "})(" + topLevel + " || " + topLevel + " = {}));\n";
	}

	private String defineLevel(ConstNode node, int indentLevel, String parent, List<String> remainingLevels) {
		final String indent = Strings.repeat("\t", indentLevel);
		String result;
		if (remainingLevels.size() == 1) {
			String name = remainingLevels.get(0);
			String entries = Joiner.on(",\n").join(Collections2.transform(node.getEntries(), new Function<ConstEntryNode, String>() {
				@Override
				public String apply(ConstEntryNode entry) {
					return indent + "\t" + entry.accept(JavaScriptConstGeneratorVisitor.this);
				}
			}));
			result = indent + parent + "." + name + " = {\n"
					+ entries + "\n"
					+ indent + "};\n";
		} else {
			String level = remainingLevels.get(0);
			result = indent + "(function(" + level + ") {\n"
						+ defineLevel(node, indentLevel + 1, level, remainingLevels.subList(1, remainingLevels.size()))
					+ indent + "})(" + parent + "." + level + " || (" + parent + "." + level + " = {}));\n"
					+ indent + "var " + level + " = " + parent + "." + level + ";\n";
		}
		return result;
	}

	@Override
	public String visitConstEntryNode(ConstEntryNode node) {
		String value = toPrimitiveString(node.getValue());
		return "\"" + node.getName() + "\": " + value;
	}

	public static String toPrimitiveString(Object value) {
		if (value instanceof String) {
			return "\"" + StringEscapeUtils.escapeJava((String) value) + "\"";
		} else {
			return String.valueOf(value);
		}
	}
}
