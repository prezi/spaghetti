package com.prezi.spaghetti.javascript;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.EnumNode;
import com.prezi.spaghetti.ast.EnumValueNode;

import java.util.List;

public class JavaScriptEnumGeneratorVisitor extends AbstractJavaScriptGeneratorVisitor {

	@Override
	public String visitEnumNode(EnumNode node) {
		return embedInPackageStructure(node, new PackagedGenerator<EnumNode>() {
			@Override
			public String generate(EnumNode node, int indentLevel, String parent) {
				final String indent = Strings.repeat("\t", indentLevel);
				List<String> values = Lists.newArrayList();
				int idx = 0;
				for (EnumValueNode value : node.getValues()) {
					values.add(indent + "\t\"" + value.getName() + "\": " + idx);
					idx++;
				}
				String entries = Joiner.on(",\n").join(values);
				return indent + parent + "." + node.getName() + " = {\n"
						+ entries + "\n"
						+ indent + "};\n";
			}
		});
	}
}
