package com.prezi.spaghetti.javascript;

import com.prezi.spaghetti.ast.ConstEntryNode;
import com.prezi.spaghetti.ast.ConstNode;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JavaScriptConstGeneratorVisitor extends AbstractJavaScriptGeneratorVisitor {

	@Override
	public String visitConstNode(ConstNode node) {
		return embedInPackageStructure(node, new PackagedGenerator<ConstNode>() {
			@Override
			public String generate(ConstNode node, int indentLevel, String parent) {
				final String indent = StringUtils.repeat("\t", indentLevel);
				List<String> entries = new ArrayList<String>();
				for (ConstEntryNode entry : node.getEntries()) {
					entries.add(indent + "\t" + entry.accept(JavaScriptConstGeneratorVisitor.this));
				}
				return String.format(
						"%s%s.%s = {\n" +
						"%s\n" +
						"%s};\n",
						indent, parent, node.getName(),
						StringUtils.join(entries, ",\n"),
						indent
				);
			}
		});
	}

	@Override
	public String visitConstEntryNode(ConstEntryNode node) {
		String value = toPrimitiveString(node.getValue());
		return String.format("\"%s\": %s", node.getName(), value);
	}

	public static String toPrimitiveString(Object value) {
		if (value instanceof String) {
			return String.format("\"%s\"", StringEscapeUtils.escapeJava((String) value));
		} else {
			return String.valueOf(value);
		}
	}
}
