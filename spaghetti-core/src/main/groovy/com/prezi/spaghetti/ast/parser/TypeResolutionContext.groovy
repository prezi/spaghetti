package com.prezi.spaghetti.ast.parser

import com.prezi.spaghetti.ast.FQName
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.Token

/**
 * Created by lptr on 29/05/14.
 */
abstract class TypeResolutionContext {
	final FQName name

	TypeResolutionContext(FQName name) {
		this.name = name
	}

	abstract public void throwError()

	public static TypeResolutionContext create(Token token) {
		return new TokenTypeResolutionContext(token)
	}

	public static TypeResolutionContext create(ModuleParser.QualifiedNameContext context) {
		return new FQNameTypeResolutionContext(context)
	}

	public TypeResolutionContext withName(FQName name) {
		return new WrappedTypeResolutionContext(this, name)
	}

	protected TypeResolutionContext() {}

	protected static class TokenTypeResolutionContext extends TypeResolutionContext {
		private final Token token

		TokenTypeResolutionContext(Token token) {
			super(FQName.fromString(token.text))
			this.token = token
		}

		@Override
		void throwError() {
			throw new InternalAstParserException(token, "Type not found: ${token.text}")
		}

		@Override
		String toString() {
			return token.text
		}
	}

	protected static class FQNameTypeResolutionContext extends TypeResolutionContext {
		private final ModuleParser.QualifiedNameContext context

		FQNameTypeResolutionContext(ModuleParser.QualifiedNameContext context) {
			super(FQName.fromString(context.text))
			this.context = context
		}

		@Override
		void throwError() {
			throw new InternalAstParserException(context, "Type not found: ${context.text}")
		}


		@Override
		String toString() {
			return context.text
		}
	}

	protected static class WrappedTypeResolutionContext extends TypeResolutionContext {
		private final TypeResolutionContext original

		WrappedTypeResolutionContext(TypeResolutionContext original, FQName name) {
			super(name)
			this.original = original
		}

		@Override
		void throwError() {
			original.throwError()
		}

		@Override
		String toString() {
			return original.toString()
		}
	}
}
