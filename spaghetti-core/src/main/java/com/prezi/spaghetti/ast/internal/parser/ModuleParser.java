package com.prezi.spaghetti.ast.internal.parser;

import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.ModuleDefinitionSource;
import com.prezi.spaghetti.ast.internal.DefaultFQName;
import com.prezi.spaghetti.ast.internal.DefaultImportNode;
import com.prezi.spaghetti.ast.internal.DefaultMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultModuleNode;
import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public class ModuleParser extends AbstractParser<DefaultModuleNode> {
	private final List<AbstractModuleTypeParser> typeParsers;
	private final List<com.prezi.spaghetti.internal.grammar.ModuleParser.MethodDefinitionContext> moduleMethodsToParse;

	public static ModuleParser create(ModuleDefinitionSource source) {
		com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleDefinitionContext context = ModuleDefinitionParser.parse(source);
		try {
			return new ModuleParser(new Locator(source), context);
		} catch (InternalAstParserException ex) {
			throw new AstParserException(source, ex.getMessage(), ex);
		} catch (Exception ex) {
			throw new AstParserException(source, "Exception while pre-parsing", ex);
		}
	}

	public ModuleParser(Locator locator, com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleDefinitionContext moduleCtx) {
		super(locator, createModuleNode(locator, moduleCtx));
		this.typeParsers = Lists.newArrayList();
		this.moduleMethodsToParse = Lists.newArrayList();

		AnnotationsParser.parseAnnotations(locator, moduleCtx.annotations(), node);
		DocumentationParser.parseDocumentation(locator, moduleCtx.documentation, node);

		for (com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleElementContext elementCtx : moduleCtx.moduleElement()) {
			if (elementCtx.importDeclaration() != null) {
				com.prezi.spaghetti.internal.grammar.ModuleParser.ImportDeclarationContext context = elementCtx.importDeclaration();
				FQName importedName = DefaultFQName.fromContext(context.qualifiedName());
				TerminalNode aliasDecl = context.Name();
				String importAlias = aliasDecl != null ? aliasDecl.getText() : importedName.getLocalName();
				DefaultImportNode importNode = new DefaultImportNode(locate(context.qualifiedName()), importedName, importAlias);
				node.getImports().add(importNode, context);
			} else if (elementCtx.externTypeDefinition() != null) {
				com.prezi.spaghetti.internal.grammar.ModuleParser.ExternTypeDefinitionContext context = elementCtx.externTypeDefinition();
				AbstractModuleTypeParser typeParser = createExternTypeDef(context);
				typeParsers.add(typeParser);
				node.getExternTypes().add(typeParser.getNode(), context);
			} else if (elementCtx.typeDefinition() != null) {
				com.prezi.spaghetti.internal.grammar.ModuleParser.TypeDefinitionContext context = elementCtx.typeDefinition();
				AbstractModuleTypeParser typeParser = createTypeDef(context, node.getName());
				typeParsers.add(typeParser);
				node.getTypes().add(typeParser.getNode(), context);
			} else if (elementCtx.methodDefinition() != null) {
				moduleMethodsToParse.add(elementCtx.methodDefinition());
			} else {
				throw new InternalAstParserException(elementCtx, "Unknown module element");
			}
		}
	}

	private static DefaultModuleNode createModuleNode(Locator locator, com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleDefinitionContext moduleCtx) {
		String moduleName = moduleCtx.qualifiedName().getText();
		List<String> nameParts = Arrays.asList(moduleCtx.qualifiedName().getText().split("\\."));
		String moduleAlias;
		if (moduleCtx.Name() != null) {
			moduleAlias = moduleCtx.Name().getText();
		} else {
			moduleAlias = StringUtils.capitalize(nameParts.get(nameParts.size() - 1)) + "Module";
		}
		return new DefaultModuleNode(locator.locate(moduleCtx.qualifiedName()), moduleName, moduleAlias);
	}

	@Override
	public DefaultModuleNode parse(TypeResolver resolver) {
		try {
			parseInternal(resolver);
			return node;
		} catch (InternalAstParserException ex) {
			throw new AstParserException(node.getSource(), ex.getMessage(), ex);
		} catch (Exception ex) {
			throw new AstParserException(node.getSource(), "Exception while pre-parsing", ex);
		}
	}

	protected void parseInternal(TypeResolver resolver) {
		// Let us use types from the local module
		resolver = new LocalModuleTypeResolver(resolver, node);

		// Parse each defined type
		for (AbstractModuleTypeParser<?, ?> parser : typeParsers) {
			parser.parse(resolver);
		}

		// Parse module methods
		for (com.prezi.spaghetti.internal.grammar.ModuleParser.MethodDefinitionContext methodCtx : moduleMethodsToParse) {
			DefaultMethodNode methodNode = MethodParser.parseMethodDefinition(locator, resolver, methodCtx);
			node.getMethods().add(methodNode, methodCtx.Name());
		}
	}

	protected AbstractModuleTypeParser createTypeDef(com.prezi.spaghetti.internal.grammar.ModuleParser.TypeDefinitionContext typeCtx, String moduleName) {
		if (typeCtx.constDefinition() != null) {
			return new ConstParser(locator, typeCtx.constDefinition(), moduleName);
		} else if (typeCtx.enumDefinition() != null) {
			return new EnumParser(locator, typeCtx.enumDefinition(), moduleName);
		} else if (typeCtx.structDefinition() != null) {
			return new StructParser(locator, typeCtx.structDefinition(), moduleName);
		} else if (typeCtx.interfaceDefinition() != null) {
			return new InterfaceParser(locator, typeCtx.interfaceDefinition(), moduleName);
		} else {
			throw new InternalAstParserException(typeCtx, "Unknown module element");
		}
	}

	protected AbstractModuleTypeParser createExternTypeDef(com.prezi.spaghetti.internal.grammar.ModuleParser.ExternTypeDefinitionContext typeCtx) {
		if (typeCtx.externInterfaceDefinition() != null) {
			return new ExternInterfaceParser(locator, typeCtx.externInterfaceDefinition());
		} else {
			throw new InternalAstParserException(typeCtx, "Unknown module element");
		}
	}

	@Override
	public String toString() {
		return "ModuleParser{" +
				"module=" + node +
				'}';
	}
}
