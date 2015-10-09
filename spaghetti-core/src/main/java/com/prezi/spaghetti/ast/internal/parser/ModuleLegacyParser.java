package com.prezi.spaghetti.ast.internal.parser;

import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.internal.DefaultFQName;
import com.prezi.spaghetti.ast.internal.DefaultImportNode;
import com.prezi.spaghetti.ast.internal.DefaultMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultModuleNode;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser;
import com.prezi.spaghetti.internal.grammar.*;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public class ModuleLegacyParser extends AbstractParser<DefaultModuleNode> {
	private final List<AbstractModuleTypeParser> typeParsers;
	private final List<org.antlr.v4.runtime.ParserRuleContext> moduleMethodsToParse;

	public static ModuleLegacyParser create(ModuleDefinitionSource source) {
		try {
			return new ModuleLegacyParser(new Locator(source), ModuleDefinitionParser.parse(source));
		} catch (InternalAstParserException ex) {
			throw new AstParserException(source, ex.getMessage(), ex);
		} catch (Exception ex) {
			throw new AstParserException(source, "Exception while pre-parsing", ex);
		}
	}

	public ModuleLegacyParser(Locator locator, com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleDefinitionContext moduleCtx) {
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

	public ModuleLegacyParser(Locator locator, com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleDefinitionLegacyContext moduleCtx) {
		super(locator, createModuleNode(locator, moduleCtx));
		this.typeParsers = Lists.newArrayList();
		this.moduleMethodsToParse = Lists.newArrayList();

		AnnotationsParser.parseAnnotations(locator, moduleCtx.annotations(), node);
		DocumentationParser.parseDocumentation(locator, moduleCtx.documentation, node);

		for (com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleElementLegacyContext elementCtx : moduleCtx.moduleElementLegacy()) {
			if (elementCtx.importDeclaration() != null) {
				com.prezi.spaghetti.internal.grammar.ModuleParser.ImportDeclarationContext context = elementCtx.importDeclaration();
				FQName importedName = DefaultFQName.fromContext(context.qualifiedName());
				TerminalNode aliasDecl = context.Name();
				String importAlias = aliasDecl != null ? aliasDecl.getText() : importedName.getLocalName();
				DefaultImportNode importNode = new DefaultImportNode(locate(context.qualifiedName()), importedName, importAlias);
				node.getImports().add(importNode, context);
			} else if (elementCtx.externTypeDefinitionLegacy() != null) {
				com.prezi.spaghetti.internal.grammar.ModuleParser.ExternTypeDefinitionLegacyContext context = elementCtx.externTypeDefinitionLegacy();
				AbstractModuleTypeParser typeParser = createExternTypeDef(context);
				typeParsers.add(typeParser);
				node.getExternTypes().add(typeParser.getNode(), context);
			} else if (elementCtx.typeDefinitionLegacy() != null) {
				com.prezi.spaghetti.internal.grammar.ModuleParser.TypeDefinitionLegacyContext context = elementCtx.typeDefinitionLegacy();
				AbstractModuleTypeParser typeParser = createTypeDef(context, node.getName());
				typeParsers.add(typeParser);
				node.getTypes().add(typeParser.getNode(), context);
			} else if (elementCtx.methodDefinitionLegacy() != null) {
				moduleMethodsToParse.add(elementCtx.methodDefinitionLegacy());
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

	private static DefaultModuleNode createModuleNode(Locator locator, com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleDefinitionLegacyContext moduleCtx) {
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
		for (org.antlr.v4.runtime.ParserRuleContext methodCtx : moduleMethodsToParse) {
			DefaultMethodNode methodNode = null;
			if (methodCtx instanceof com.prezi.spaghetti.internal.grammar.ModuleParser.MethodDefinitionContext) {
				methodNode = MethodParser.parseMethodDefinition(locator, resolver, (com.prezi.spaghetti.internal.grammar.ModuleParser.MethodDefinitionContext)methodCtx);
				node.getMethods().add(methodNode, ((com.prezi.spaghetti.internal.grammar.ModuleParser.MethodDefinitionContext)methodCtx).Name());
			} else if (methodCtx instanceof com.prezi.spaghetti.internal.grammar.ModuleParser.MethodDefinitionLegacyContext) {
				methodNode = MethodLegacyParser.parseMethodDefinition(locator, resolver, (com.prezi.spaghetti.internal.grammar.ModuleParser.MethodDefinitionLegacyContext)methodCtx);
				node.getMethods().add(methodNode, ((com.prezi.spaghetti.internal.grammar.ModuleParser.MethodDefinitionLegacyContext)methodCtx).Name());
			} else {
				throw new RuntimeException("Unknown type in moduleMethodsToParse.");
			}
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

	protected AbstractModuleTypeParser createTypeDef(com.prezi.spaghetti.internal.grammar.ModuleParser.TypeDefinitionLegacyContext typeCtx, String moduleName) {
		if (typeCtx.constDefinitionLegacy() != null) {
			return new ConstLegacyParser(locator, typeCtx.constDefinitionLegacy(), moduleName);
		} else if (typeCtx.enumDefinitionLegacy() != null) {
			return new EnumLegacyParser(locator, typeCtx.enumDefinitionLegacy(), moduleName);
		} else if (typeCtx.structDefinitionLegacy() != null) {
			return new StructLegacyParser(locator, typeCtx.structDefinitionLegacy(), moduleName);
		} else if (typeCtx.interfaceDefinitionLegacy() != null) {
			return new InterfaceLegacyParser(locator, typeCtx.interfaceDefinitionLegacy(), moduleName);
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

	protected AbstractModuleTypeParser createExternTypeDef(com.prezi.spaghetti.internal.grammar.ModuleParser.ExternTypeDefinitionLegacyContext typeCtx) {
		if (typeCtx.externInterfaceDefinitionLegacy() != null) {
			return new ExternInterfaceLegacyParser(locator, typeCtx.externInterfaceDefinitionLegacy());
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
