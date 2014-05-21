package com.prezi.spaghetti.haxe;

import groovy.lang.Closure;
import org.antlr.v4.runtime.RuleContext;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

import java.util.ArrayList;
import java.util.Arrays;

/*
 * Note: This is in Java, because otherwise the Groovy compiler doesn't find it.
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class WithDeprecationTransformation implements ASTTransformation {
	@Override
	public void visit(ASTNode[] astNodes, SourceUnit sourceUnit)
	{
		if (!DefaultGroovyMethods.asBoolean(astNodes)) return;

		if (!DefaultGroovyMethods.asBoolean(astNodes[0])) return;

		if (!DefaultGroovyMethods.asBoolean(astNodes[1])) return;

		if (!(astNodes[0] instanceof AnnotationNode)) return;

		AnnotationNode annotation = DefaultGroovyMethods.asType(astNodes[0], AnnotationNode.class);
		final ClassNode node = annotation.getClassNode();
		if (!node.getName().equals(WithDeprecation.class.getName())) return;

		if (!(astNodes[1] instanceof MethodNode)) return;

		final MethodNode method = DefaultGroovyMethods.asType(astNodes[1], MethodNode.class);

		if (!method.getReturnType().getTypeClass().equals(String.class))
		{
			addError("Invalid return type for @WithDeprecation: " + String.valueOf(method.getReturnType()) + ", should be String", annotation, sourceUnit);
		}


		if (method.getParameters().length != 1)
		{
			addError("Invalid number of parameters to @WithDeprecation method: " + String.valueOf(method.getParameters().length) + ", should be a single RuleContext", annotation, sourceUnit);
		}


		Parameter ctxParam = method.getParameters()[0];
		final ClassNode paramType = ctxParam.getType();

		if (!paramType.isDerivedFrom(new ClassNode(RuleContext.class)))
		{
			addError("Invalid parameters for @WithDeprecation: " + String.valueOf(paramType) + ", should be a single RuleContext", annotation, sourceUnit);
		}


		if (!DefaultGroovyMethods.asBoolean(paramType.getMethod("annotations", new Parameter[0])))
		{
			addError("@WithDeprecation method only works on contexts that have an \'annotations\' method, but not on " + String.valueOf(paramType), annotation, sourceUnit);
		}


		// Wrap the original method code in a closure
		// 		{ -> ... }
		ClosureExpression closure = new ClosureExpression(Parameter.EMPTY_ARRAY, method.getCode());

		// Call the closure
		// 		{ -> ... }()
		MethodCallExpression closureCall = new MethodCallExpression(closure, "call", ArgumentListExpression.EMPTY_ARGUMENTS);

		// Get documentation from context
		//		ctx.annotations()
		MethodCallExpression ctxAnnotations = new MethodCallExpression(new VariableExpression(ctxParam.getName()), "annotations", ArgumentListExpression.EMPTY_ARGUMENTS);

		// Build argument list
		// 		(ctx.annotations(), { -> ... }())
		ArgumentListExpression arguments = new ArgumentListExpression(ctxAnnotations, closureCall);

		// Call the ModuleUtils with argument list
		// 		Deprecation.formatDeprecationWithAutoPrefix(ctx.annotations(), { -> ... }())
		StaticMethodCallExpression callModuleUtils = new StaticMethodCallExpression(new ClassNode(Deprecation.class), "formatDeprecationWithAutoPrefix", arguments);

		// Prepend return statement and replace original method code
		//		return Deprecation.formatDeprecationWithAutoPrefix(ctx.annotations(), { -> ... }())
		method.setCode(new BlockStatement(Arrays.asList((Statement) new ReturnStatement(callModuleUtils)), method.getVariableScope()));

		visitVariableScopes(sourceUnit);
	}

	/**
	 * Fix the variable scopes for closures.  Without this closures will be missing the input params being passed from the parent scope.
	 *
	 * @param sourceUnit The SourceUnit to visit and add the variable scopes.
	 */
	private static void visitVariableScopes(SourceUnit sourceUnit)
	{
		final VariableScopeVisitor scopeVisitor = new VariableScopeVisitor(sourceUnit);
		DefaultGroovyMethods.each(sourceUnit.getAST().getClasses(), new Closure<Object>(null, null) {
			public void doCall(ClassNode it)
			{
				scopeVisitor.visitClass(it);
			}

			public void doCall()
			{
				doCall(null);
			}

		});
	}

	private static void addError(String msg, ASTNode expr, SourceUnit source)
	{
		int line = expr.getLineNumber();
		int col = expr.getColumnNumber();
		SyntaxException se = new SyntaxException(msg + "\n", line, col);
		SyntaxErrorMessage sem = new SyntaxErrorMessage(se, source);
		source.getErrorCollector().addErrorAndContinue(sem);
	}

}
