package com.prezi.spaghetti.ast.internal.parser

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.QualifiedTypeNode
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser
import com.prezi.spaghetti.definition.internal.ModuleParserContext
import com.prezi.spaghetti.internal.grammar.ModuleParser
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.misc.NotNull
import org.antlr.v4.runtime.misc.Nullable

class AstParserSpecification extends AstSpecification {
	static ModuleParser parser(String data) {
		ModuleDefinitionParser.createParser(ModuleDefinitionSource.fromString("test", data)).parser
	}

	static ModuleParser parser(Locator locator) {
		def failTestListener = new BaseErrorListener() {
			@Override
			void syntaxError(
					@NotNull Recognizer recognizer,
					@Nullable Object offendingSymbol, int line, int charPositionInLine,
					@NotNull String msg, @Nullable RecognitionException e) {
				throw new RuntimeException("Cannot parse input", e)
			}
		}
		def parserContext = ModuleDefinitionParser.createParser(locator.source)
		parserContext.lexer.addErrorListener failTestListener
		parserContext.parser.addErrorListener failTestListener
		parserContext.parser
	}

	static TypeResolver resolver(QualifiedTypeNode... nodes) {
		return new SimpleNamedTypeResolver(MissingTypeResolver.INSTANCE, Arrays.asList(nodes))
	}
}
