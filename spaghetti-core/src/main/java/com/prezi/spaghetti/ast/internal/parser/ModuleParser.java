package com.prezi.spaghetti.ast.internal.parser;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeResolver;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.internal.DefaultFQName;
import com.prezi.spaghetti.ast.internal.DefaultImportNode;
import com.prezi.spaghetti.ast.internal.DefaultMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultModuleNode;
import com.prezi.spaghetti.bundle.DefinitionLanguage;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

abstract public class ModuleParser extends AbstractParser<DefaultModuleNode> {
	protected ModuleParser(Locator locator, DefaultModuleNode node) {
		super(locator, node);
	}
	public static ModuleParser create(ModuleDefinitionSource source) {
		if (source.getDefinitionLanguage() == DefinitionLanguage.TypeScript) {
			return new SimpleTypeScriptDefinitionParser(source);
		}

		com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleDefinitionContext context = ModuleDefinitionParser.parse(source);
		try {
			return new DefaultModuleParser(new Locator(source), context);
		} catch (InternalAstParserException ex) {
			throw new AstParserException(source, ex.getMessage(), ex);
		} catch (Exception ex) {
			throw new AstParserException(source, "Exception while pre-parsing", ex);
		}
	}
}
