package com.prezi.spaghetti.ast.parser;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.internal.DefaultDocumentationNode;
import com.prezi.spaghetti.ast.internal.MutableDocumentedNode;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class DocumentationParser {
	public static void parseDocumentation(Token documentation, MutableDocumentedNode node) {
		if (documentation != null) {
			String text = documentation.getText();
			List<String> lines = Lists.newArrayList(text.substring(3, text.length() - 3).trim().split("\\r?\\n?\\s*\\*\\s?"));
			while (!lines.isEmpty() && Strings.isNullOrEmpty(lines.get(0))) {
				lines.remove(0);
			}

			while (!lines.isEmpty() && Strings.isNullOrEmpty(lines.get(lines.size() - 1))) {
				lines.remove(lines.size() - 1);
			}

			node.setDocumentation(new DefaultDocumentationNode(lines));
		}
	}
}
