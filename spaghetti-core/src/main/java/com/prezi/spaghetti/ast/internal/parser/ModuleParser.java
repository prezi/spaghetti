package com.prezi.spaghetti.ast.internal.parser;

import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.ast.internal.DefaultImportNode;
import com.prezi.spaghetti.ast.internal.DefaultMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultModuleNode;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

public class ModuleParser extends AbstractParser {
	private final List<AbstractModuleTypeParser> typeParsers;
	private final List<com.prezi.spaghetti.internal.grammar.ModuleParser.MethodDefinitionContext> moduleMethodsToParse;
	private final DefaultModuleNode module;

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

	protected ModuleParser(Locator locator, com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleDefinitionContext moduleCtx) {
		super(locator);
		this.typeParsers = Lists.newArrayList();
		this.moduleMethodsToParse = Lists.newArrayList();

		String moduleName = moduleCtx.qualifiedName().getText();
		List<String> nameParts = Arrays.asList(moduleCtx.qualifiedName().getText().split("\\."));
		String moduleAlias;
		if (moduleCtx.Name() != null) {
			moduleAlias = moduleCtx.Name().getText();
		} else {
			moduleAlias = StringUtils.capitalize(nameParts.get(nameParts.size() - 1)) + "Module";
		}
		this.module = new DefaultModuleNode(locate(moduleCtx.qualifiedName()), moduleName, moduleAlias);
		AnnotationsParser.parseAnnotations(locator, moduleCtx.annotations(), module);
		DocumentationParser.parseDocumentation(locator, moduleCtx.documentation, module);

		for (com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleElementContext elementCtx : moduleCtx.moduleElement()) {
			if (elementCtx.importDeclaration() != null) {
				com.prezi.spaghetti.internal.grammar.ModuleParser.ImportDeclarationContext context = elementCtx.importDeclaration();
				FQName importedName = FQName.fromContext(context.qualifiedName());
				TerminalNode aliasDecl = context.Name();
				String importAlias = aliasDecl != null ? aliasDecl.getText() : importedName.localName;
				DefaultImportNode importNode = new DefaultImportNode(locate(context.qualifiedName()), importedName, importAlias);
				module.getImports().add(importNode, context);
			} else if (elementCtx.externTypeDefinition() != null) {
				com.prezi.spaghetti.internal.grammar.ModuleParser.ExternTypeDefinitionContext context = elementCtx.externTypeDefinition();
				AbstractModuleTypeParser typeParser = createExternTypeDef(context);
				typeParsers.add(typeParser);
				module.getExternTypes().add(typeParser.getNode(), context);
			} else if (elementCtx.typeDefinition() != null) {
				com.prezi.spaghetti.internal.grammar.ModuleParser.TypeDefinitionContext context = elementCtx.typeDefinition();
				AbstractModuleTypeParser typeParser = createTypeDef(context, moduleName);
				typeParsers.add(typeParser);
				module.getTypes().add(typeParser.getNode(), context);
			} else if (elementCtx.methodDefinition() != null) {
				moduleMethodsToParse.add(elementCtx.methodDefinition());
			} else {
				throw new InternalAstParserException(elementCtx, "Unknown module element");
			}
		}
	}

	public ModuleNode parse(TypeResolver resolver) {
		try {
			return parseInternal(resolver);
		} catch (InternalAstParserException ex) {
			throw new AstParserException(module.getSource(), ex.getMessage(), ex);
		} catch (Exception ex) {
			throw new AstParserException(module.getSource(), "Exception while pre-parsing", ex);
		}
	}

	protected DefaultModuleNode parseInternal(TypeResolver resolver) {
		// Let us use types from the local module
		resolver = new LocalModuleTypeResolver(resolver, module);

		// Parse each defined type
		for (AbstractModuleTypeParser<?, ?> parser : typeParsers) {
			parser.parse(resolver);
		}

		// Parse module methods
		for (com.prezi.spaghetti.internal.grammar.ModuleParser.MethodDefinitionContext methodCtx : moduleMethodsToParse) {
			DefaultMethodNode methodNode = MethodParser.parseMethodDefinition(locator, resolver, methodCtx);
			module.getMethods().add(methodNode, methodCtx.Name());
		}

		return module;
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

	public final DefaultModuleNode getModule() {
		return module;
	}

	@Override
	public String toString() {
		return "ModuleParser{" +
				"module=" + module +
				'}';
	}
}
