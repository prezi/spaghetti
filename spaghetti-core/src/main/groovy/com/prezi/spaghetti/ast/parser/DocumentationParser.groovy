package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.internal.DefaultDocumentationNode
import com.prezi.spaghetti.ast.internal.MutableDocumentedNode
import org.antlr.v4.runtime.Token

/**
 * Created by lptr on 30/05/14.
 */
class DocumentationParser {
	public static void parseDocumentation(Token documentation, MutableDocumentedNode node) {
		if (documentation) {
			List<String> lines = documentation.text[3..-3].trim().split(/\r?\n?\s*\*\s?/)
			while (lines && !lines.first()) {
				lines.remove(0)
			}
			while (lines && !lines.last()) {
				lines.remove(lines.size() - 1)
			}
			node.documentation = new DefaultDocumentationNode(lines)
		}
	}
}
