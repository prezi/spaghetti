package com.prezi.spaghetti.ast.internal.parser;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.ast.QualifiedTypeNode;
import com.prezi.spaghetti.ast.internal.DefaultExternInterfaceNode;
import com.prezi.spaghetti.ast.internal.DefaultImportNode;
import com.prezi.spaghetti.ast.internal.DefaultMethodNode;
import com.prezi.spaghetti.ast.internal.DefaultModuleNode;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ModuleParser {
	private static final Set<QualifiedTypeNode> DEFAULT_EXTERNS = ImmutableSet.<QualifiedTypeNode> builder()
			.add(new DefaultExternInterfaceNode(FQName.fromString("SpaghettiParameters")))
			.build();

	private final List<AbstractModuleTypeParser> typeParsers;
	private final List<com.prezi.spaghetti.internal.grammar.ModuleParser.MethodDefinitionContext> moduleMethodsToParse;
	private final DefaultModuleNode module;

	public static ModuleParser create(ModuleDefinitionSource source) {
		com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleDefinitionContext context = ModuleDefinitionParser.parse(source);
		try {
			return new ModuleParser(source, context);
		} catch (InternalAstParserException ex) {
			throw new AstParserException(source, ex.getMessage(), ex);
		} catch (Exception ex) {
			throw new AstParserException(source, "Exception while pre-parsing", ex);
		}
	}

	protected ModuleParser(ModuleDefinitionSource source, com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleDefinitionContext moduleCtx) {
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
		this.module = new DefaultModuleNode(moduleName, moduleAlias, source);
		//noinspection deprecation
		module.getExternTypes().addAll(DEFAULT_EXTERNS);

		AnnotationsParser.parseAnnotations(moduleCtx.annotations(), module);
		DocumentationParser.parseDocumentation(moduleCtx.documentation, module);

		for (com.prezi.spaghetti.internal.grammar.ModuleParser.ModuleElementContext elementCtx : moduleCtx.moduleElement()) {
			if (elementCtx.importDeclaration() != null) {
				FQName importedName = FQName.fromContext(elementCtx.importDeclaration().qualifiedName());
				TerminalNode aliasDecl = elementCtx.importDeclaration().Name();
				String importAlias = aliasDecl != null ? aliasDecl.getText() : importedName.localName;
				DefaultImportNode importNode = new DefaultImportNode(importedName, importAlias);
				module.getImports().put(FQName.fromString(null, importAlias), importNode);
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
			DefaultMethodNode methodNode = MethodParser.parseMethodDefinition(resolver, methodCtx);
			module.getMethods().add(methodNode, methodCtx.Name());
		}

		return module;
	}

	protected static AbstractModuleTypeParser createTypeDef(com.prezi.spaghetti.internal.grammar.ModuleParser.TypeDefinitionContext typeCtx, String moduleName) {
		if (typeCtx.constDefinition() != null) {
			return new ConstParser(typeCtx.constDefinition(), moduleName);
		} else if (typeCtx.enumDefinition() != null) {
			return new EnumParser(typeCtx.enumDefinition(), moduleName);
		} else if (typeCtx.structDefinition() != null) {
			return new StructParser(typeCtx.structDefinition(), moduleName);
		} else if (typeCtx.interfaceDefinition() != null) {
			return new InterfaceParser(typeCtx.interfaceDefinition(), moduleName);
		} else {
			throw new InternalAstParserException(typeCtx, "Unknown module element");
		}
	}

	protected static AbstractModuleTypeParser createExternTypeDef(com.prezi.spaghetti.internal.grammar.ModuleParser.ExternTypeDefinitionContext typeCtx) {
		if (typeCtx.externInterfaceDefinition() != null) {
			return new ExternInterfaceParser(typeCtx.externInterfaceDefinition());
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
