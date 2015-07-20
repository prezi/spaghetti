package com.prezi.spaghetti.javascript;

import com.prezi.spaghetti.ast.EnumNode;
import com.prezi.spaghetti.ast.EnumValueNode;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class JavaScriptEnumGeneratorVisitor extends AbstractJavaScriptGeneratorVisitor {

	@Override
	public String visitEnumNode(EnumNode node) {
		return embedInPackageStructure(node, new PackagedGenerator<EnumNode>() {
			@Override
			public String generate(EnumNode node, int indentLevel, String parent) {
				final String indent = StringUtils.repeat("\t", indentLevel);
				List<String> values = new ArrayList<String>();
				for (EnumValueNode value : node.getValues()) {
					values.add(String.format("%s\t\"%s\": %d", indent, value.getName(), value.getValue()));
				}
				String entries = StringUtils.join(values, ",\n");
				return String.format(
						"%s%s.%s = {\n" +
						"%s\n" +
						"%s};\n",
						indent, parent, node.getName(),
						entries,
						indent
				);
			}
		});
	}
}
