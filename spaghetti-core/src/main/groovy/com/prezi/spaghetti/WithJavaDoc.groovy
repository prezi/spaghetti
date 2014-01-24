package com.prezi.spaghetti

import org.antlr.v4.runtime.RuleContext
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.PropertyExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.classgen.VariableScopeVisitor
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.messages.SyntaxErrorMessage
import org.codehaus.groovy.syntax.SyntaxException
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Automatically prepend JavaDoc to generated code.
 */

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
@GroovyASTTransformationClass(classes = [ Transform ])
public @interface WithJavaDoc {
}

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class Transform implements ASTTransformation {

	@Override
	void visit(ASTNode[] astNodes, SourceUnit sourceUnit)
	{
		if (!astNodes) return
		if (!astNodes[0]) return
		if (!astNodes[1]) return
		if (!(astNodes[0] instanceof AnnotationNode)) return
		AnnotationNode annotation = astNodes[0] as AnnotationNode
		if (annotation.classNode?.name != WithJavaDoc.class.getName()) return
		if (!(astNodes[1] instanceof MethodNode)) return
		MethodNode method = astNodes[1] as MethodNode

		if (method.returnType.typeClass != String)
		{
			addError("Invalid return type for @WithJavaDoc: ${method.returnType}, should be String", annotation, sourceUnit)
		}

		if (method.parameters.length != 1)
		{
			addError("Invalid number of parameters to @WithJavaDoc method: ${method.parameters.length}, should be a single RuleContext", annotation, sourceUnit)
		}

		def ctxParam = method.parameters[0]
		def paramType = ctxParam.type

		if (!paramType.isDerivedFrom(new ClassNode(RuleContext))) {
			addError("Invalid parameters for @WithJavaDoc: ${paramType}, should be a single RuleContext", annotation, sourceUnit)
		}

		if (!paramType.getField("documentation"))
		{
			addError("@WithJavaDoc method only works on contexts that have a 'documentation' property, but not on ${paramType}", annotation, sourceUnit)
		}

		def closure = new ClosureExpression(Parameter.EMPTY_ARRAY, method.code)

		def arguments = new ArgumentListExpression([
				new PropertyExpression(
						new VariableExpression(ctxParam.name),
						"documentation"),
				new MethodCallExpression(closure, "call", ArgumentListExpression.EMPTY_ARGUMENTS)
		])

		method.code = new BlockStatement([
				new ReturnStatement(
						new StaticMethodCallExpression(
								new ClassNode(ModuleUtils),
								"formatDocumentationWithAutoPrefix",
								arguments)
				)
		], method.variableScope)
		visitVariableScopes(sourceUnit)
	}

	/**
	 * Fix the variable scopes for closures.  Without this closures will be missing the input params being passed from the parent scope.
	 * @param sourceUnit The SourceUnit to visit and add the variable scopes.
	 */
	private static void visitVariableScopes(SourceUnit sourceUnit)
	{
		VariableScopeVisitor scopeVisitor = new VariableScopeVisitor(sourceUnit);
		sourceUnit.AST.classes.each {
			scopeVisitor.visitClass(it)
		}
	}

	private static void addError(String msg, ASTNode expr, SourceUnit source) {
	    int line = expr.lineNumber
	    int col = expr.columnNumber
	    SyntaxException se = new SyntaxException(msg + '\n', line, col)
	    SyntaxErrorMessage sem = new SyntaxErrorMessage(se, source)
	    source.errorCollector.addErrorAndContinue(sem)
	}
}
