package com.prezi.spaghetti.ast;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ModuleVisitorBase<T> implements ModuleVisitor<T> {
	@Override
	public T visitAnnotationNode(AnnotationNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitDocumentationNode(DocumentationNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitConstEntryNode(ConstEntryNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitConstNode(ConstNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitEnumNode(EnumNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitEnumReference(EnumReference reference) {
		return visitChildren(reference);
	}

	@Override
	public T visitEnumValueNode(EnumValueNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitExternInterfaceNode(ExternInterfaceNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitExternInterfaceReference(ExternInterfaceReference reference) {
		return visitChildren(reference);
	}

	@Override
	public T visitImportNode(ImportNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitInterfaceNode(InterfaceNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitInterfaceReference(InterfaceReference reference) {
		return visitChildren(reference);
	}

	@Override
	public T visitMethodNode(MethodNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitMethodParameterNode(MethodParameterNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitModuleNode(ModuleNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitPrimitiveTypeReference(PrimitiveTypeReference reference) {
		return visitChildren(reference);
	}

	@Override
	public T visitPropertyNode(PropertyNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitStructNode(StructNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitStructReference(StructReference reference) {
		return visitChildren(reference);
	}

	@Override
	public T visitFunctionType(FunctionType node) {
		return visitChildren(node);
	}

	@Override
	public T visitTypeParameterNode(TypeParameterNode node) {
		return visitChildren(node);
	}

	@Override
	public T visitTypeParameterReference(TypeParameterReference reference) {
		return visitChildren(reference);
	}

	@Override
	public T visitVoidTypeReference(VoidTypeReference reference) {
		return visitChildren(reference);
	}

	public T visitChildren(AstNode node) {
		T result = defaultResult();
		for (AstNode child : node.getChildren()) {
			T childResult = visit(child);
			result = aggregateResult(result, childResult);
		}

		return result;
	}

	private final LinkedList<AstNode> stackInternal = new LinkedList<AstNode>();
	protected final List<AstNode> stack = Collections.unmodifiableList(stackInternal);

	protected AstNode getParentNode() {
		return stackInternal.isEmpty() ? null : stackInternal.getFirst();
	}

	protected boolean hasAncestor(Class<? extends AstNode> type) {
		return findAncestor(type) != null;
	}

	protected boolean hasAncestor(Class<? extends AstNode> type, int startDepth) {
		return findAncestor(type, startDepth) != null;
	}

	protected <N extends AstNode> N findAncestor(Class<N> type) {
		return findAncestorInternal(stackInternal, type);
	}

	protected <N extends AstNode> N findAncestor(Class<N> type, int startDepth) {
		return findAncestorInternal(stackInternal.subList(startDepth, stackInternal.size()), type);
	}

	@SuppressWarnings("unchecked")
	private static <N extends AstNode> N findAncestorInternal(List<AstNode> stack, final Class<N> type) {
		N result = null;
		for (AstNode node : stack) {
			if (type.isAssignableFrom(node.getClass())) {
				result = (N) node;
				break;
			}
		}
		return result;
	}

	@Override
	public T beforeVisit(AstNode node) {
		stackInternal.add(0, node);
		return defaultResult();
	}

	@Override
	public T afterVisit(AstNode node, T result) {
		stackInternal.remove(0);
		return result;
	}

	@Override
	public T visit(AstNode node) {
		return (T) node.accept(this);
	}

	protected T defaultResult() {
		return null;
	}

	@Override
	public T aggregateResult(T aggregate, T nextResult) {
		return nextResult;
	}
}
