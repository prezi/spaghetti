package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.ast.ModuleMethodType
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.ast.internal.DefaultExternNode
import com.prezi.spaghetti.ast.internal.DefaultImportNode
import com.prezi.spaghetti.ast.internal.DefaultModuleMethodNode
import com.prezi.spaghetti.ast.internal.DefaultModuleNode
import com.prezi.spaghetti.definition.ModuleDefinitionParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.grammar.ModuleParser.ModuleDefinitionContext
import com.prezi.spaghetti.grammar.ModuleParser.ModuleElementContext
import com.prezi.spaghetti.grammar.ModuleParser.ModuleMethodDefinitionContext
import com.prezi.spaghetti.grammar.ModuleParser.TypeDefinitionContext

/**
 * Created by lptr on 27/05/14.
 */
class ModuleParser {
	private final ModuleDefinitionContext moduleCtx
	private final List<AbstractModuleTypeParser> typeParsers
	private final List<ModuleMethodDefinitionContext> moduleMethodsToParse
	final DefaultModuleNode module

	static ModuleParser create(ModuleDefinitionSource source) {
		def context = ModuleDefinitionParser.parse(source)
		try {
			return new ModuleParser(source, context)
		} catch (InternalAstParserException ex) {
			throw new AstParserException(source, ex.message, ex)
		} catch (Exception ex) {
			throw new AstParserException(source, "Exception while pre-parsing", ex)
		}
	}

	protected ModuleParser(ModuleDefinitionSource source, ModuleDefinitionContext moduleCtx) {
		this.moduleCtx = moduleCtx
		this.typeParsers = []
		this.moduleMethodsToParse = []

		def moduleName = moduleCtx.qualifiedName().text
		def moduleAlias = moduleCtx.Name() ? moduleCtx.Name().text : moduleCtx.qualifiedName().text.split(/\./).last().capitalize()
		this.module = new DefaultModuleNode(moduleName, moduleAlias, source)
		AnnotationsParser.parseAnnotations(moduleCtx.annotations(), module)
		DocumentationParser.parseDocumentation(moduleCtx.documentation, module)

		moduleCtx.moduleElement().each { ModuleElementContext elementCtx ->
			if (elementCtx.importDeclaration()) {
				def importedName = FQName.fromContext(elementCtx.importDeclaration().qualifiedName())
				def importAlias = elementCtx.importDeclaration().Name()?.text ?: importedName.localName
				def importNode = new DefaultImportNode(importedName, importAlias)
				module.imports.put(FQName.fromString(null, importAlias), importNode)
			} else if (elementCtx.externTypeDefinition()) {
				def context = elementCtx.externTypeDefinition()
				def fqName = FQName.fromContext(context.qualifiedName())
				def extern = new DefaultExternNode(fqName)
				module.externs.add extern, context
			} else if (elementCtx.typeDefinition()) {
				def context = elementCtx.typeDefinition()
				def typeParser = createTypeDef(context, moduleName)
				typeParsers.add(typeParser)
				module.types.add typeParser.node, context
			} else if (elementCtx.moduleMethodDefinition()) {
				moduleMethodsToParse.add(elementCtx.moduleMethodDefinition())
			} else {
				throw new InternalAstParserException(elementCtx, "Unknown module element")
			}
		}
	}

	ModuleNode parse(TypeResolver resolver) {
		try {
			return parseInternal(resolver)
		} catch (InternalAstParserException ex) {
			throw new AstParserException(module.source, ex.message, ex)
		} catch (Exception ex) {
			throw new AstParserException(module.source, "Exception while pre-parsing", ex)
		}
	}

	protected DefaultModuleNode parseInternal(TypeResolver resolver) {
		// Let us use types from the local module
		resolver = new LocalModuleTypeResolver(resolver, module)

		// Parse each defined type
		typeParsers.each { AbstractModuleTypeParser parser -> parser.parse(resolver) }

		// Parse module methods
		moduleMethodsToParse.each { ModuleMethodDefinitionContext methodCtx ->
			def nameCtx = methodCtx.methodDefinition().Name()
			def methodName = nameCtx.text
			def methodType = methodCtx.isStatic ? ModuleMethodType.STATIC : ModuleMethodType.DYNAMIC
			def method = new DefaultModuleMethodNode(methodName, methodType)
			AnnotationsParser.parseAnnotations(methodCtx.annotations(), method)
			DocumentationParser.parseDocumentation(methodCtx.documentation, method)
			MethodParser.parseMethodDefinition(resolver, methodCtx.methodDefinition(), method)
			module.methods.add method, nameCtx
		}

		return module
	}

	protected static AbstractModuleTypeParser createTypeDef(TypeDefinitionContext typeCtx, String moduleName) {
		if (typeCtx.constDefinition()) {
			return new ConstParser(typeCtx.constDefinition(), moduleName)
		} else if (typeCtx.enumDefinition()) {
			return new EnumParser(typeCtx.enumDefinition(), moduleName)
		} else if (typeCtx.structDefinition()) {
			return new StructParser(typeCtx.structDefinition(), moduleName)
		} else if (typeCtx.interfaceDefinition()) {
			return new InterfaceParser(typeCtx.interfaceDefinition(), moduleName)
		} else {
			throw new InternalAstParserException(typeCtx, "Unknown module element")
		}
	}
}
