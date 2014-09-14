package com.prezi.spaghetti.ast.internal.parser;

import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.internal.grammar.ModuleParser;
import org.antlr.v4.runtime.Token;

public abstract class TypeResolutionContext {

	private final FQName name;

	protected TypeResolutionContext(FQName name) {
		this.name = name;
	}

	public abstract void throwError();

	public static TypeResolutionContext create(Token token) {
		return new TokenTypeResolutionContext(token);
	}

	public static TypeResolutionContext create(ModuleParser.QualifiedNameContext context) {
		return new FQNameTypeResolutionContext(context);
	}

	public TypeResolutionContext withName(FQName name) {
		return new WrappedTypeResolutionContext(this, name);
	}

	public final FQName getName() {
		return name;
	}

	protected static class TokenTypeResolutionContext extends TypeResolutionContext {

		private final Token token;

		public TokenTypeResolutionContext(Token token) {
			super(FQName.fromString(token.getText()));
			this.token = token;
		}

		@Override
		public void throwError() {
			throw new InternalAstParserException(token, "Type not found: " + token.getText());
		}

		@Override
		public String toString() {
			return token.getText();
		}
	}

	protected static class FQNameTypeResolutionContext extends TypeResolutionContext {

		private final ModuleParser.QualifiedNameContext context;

		public FQNameTypeResolutionContext(ModuleParser.QualifiedNameContext context) {
			super(FQName.fromString(context.getText()));
			this.context = context;
		}

		@Override
		public void throwError() {
			throw new InternalAstParserException(context, "Type not found: " + context.getText());
		}

		@Override
		public String toString() {
			return context.getText();
		}
	}

	protected static class WrappedTypeResolutionContext extends TypeResolutionContext {

		private final TypeResolutionContext original;

		public WrappedTypeResolutionContext(TypeResolutionContext original, FQName name) {
			super(name);
			this.original = original;
		}

		@Override
		public void throwError() {
			original.throwError();
		}

		@Override
		public String toString() {
			return original.toString();
		}
	}
}
