package com.prezi.spaghetti.javascript;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.prezi.spaghetti.ast.ConstEntryNode;
import com.prezi.spaghetti.ast.ConstNode;
import org.apache.commons.lang.StringEscapeUtils;

public class JavaScriptConstGeneratorVisitor extends AbstractJavaScriptGeneratorVisitor {

	@Override
	public String visitConstNode(ConstNode node) {
		return embedInPackageStructure(node, new PackagedGenerator<ConstNode>() {
			@Override
			public String generate(ConstNode node, int indentLevel, String parent) {
				final String indent = Strings.repeat("\t", indentLevel);
				String entries = Joiner.on(",\n").join(Collections2.transform(node.getEntries(), new Function<ConstEntryNode, String>() {
					@Override
					public String apply(ConstEntryNode entry) {
						return indent + "\t" + entry.accept(JavaScriptConstGeneratorVisitor.this);
					}
				}));
				return indent + parent + "." + node.getName() + " = {\n"
						+ entries + "\n"
						+ indent + "};\n";
			}
		});
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
