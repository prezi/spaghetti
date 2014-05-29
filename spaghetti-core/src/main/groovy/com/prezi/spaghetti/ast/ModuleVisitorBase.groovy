package com.prezi.spaghetti.ast

/**
 * Created by lptr on 30/05/14.
 */
class ModuleVisitorBase<T> implements ModuleVisitor<T> {
	@Override T visitAnnotationNode(AnnotationNode node) { visitChildren(node) }

	@Override T visitDocumentationNode(DocumentationNode node) { visitChildren(node) }

	@Override T visitConstEntryNode(ConstEntryNode node) { visitChildren(node) }

	@Override T visitConstNode(ConstNode node) { visitChildren(node) }

	@Override T visitEnumNode(EnumNode node) { visitChildren(node) }

	@Override T visitEnumReference(EnumReference reference) { visitChildren(reference) }

	@Override T visitEnumValueNode(EnumValueNode node) { visitChildren(node) }

	@Override T visitExternNode(ExternNode node) { visitChildren(node) }

	@Override T visitExternReference(ExternReference reference) { visitChildren(reference) }

	@Override T visitImportNode(ImportNode node) { visitChildren(node) }

	@Override T visitInterfaceMethodNode(InterfaceMethodNode node) { visitChildren(node) }

	@Override T visitInterfaceNode(InterfaceNode node) { visitChildren(node) }

	@Override T visitInterfaceReference(InterfaceReference reference) { visitChildren(reference) }

	@Override T visitMethodParameterNode(MethodParameterNode node) { visitChildren(node) }

	@Override T visitModuleMethodNode(ModuleMethodNode node) { visitChildren(node) }

	@Override T visitModuleNode(ModuleNode node) { visitChildren(node) }

	@Override T visitPrimitiveTypeReference(PrimitiveTypeReference reference) { visitChildren(reference) }

	@Override T visitPropertyNode(PropertyNode node) { visitChildren(node) }

	@Override T visitStructNode(StructNode node) { visitChildren(node) }

	@Override T visitStructReference(StructReference reference) { visitChildren(reference) }

	@Override T visitTypeChain(TypeChain node) { visitChildren(node) }

	@Override T visitTypeParameterNode(TypeParameterNode node) { visitChildren(node) }

	@Override T visitTypeParameterReference(TypeParameterReference reference) { visitChildren(reference) }

	@Override T visitVoidTypeReference(VoidTypeReference reference) { visitChildren(reference) }

	T visitChildren(AstNode node) {
		T result = defaultResult()
		node.children.each { AstNode child ->
			T childResult = visit(child);
			result = aggregateResult(result, childResult);
		}
		return result
	}

	private final LinkedList<AstNode> stackInternal = new LinkedList<>()
	protected final List<AstNode> stack = stackInternal.asImmutable()

	protected AstNode getParentNode() {
		return stackInternal.empty ? null : stackInternal.first()
	}

	protected boolean hasAncestor(Class<? extends AstNode> type) {
		return findAncestor(type) != null
	}

	protected boolean hasAncestor(Class<? extends AstNode> type, int startDepth) {
		return findAncestor(type, startDepth) != null
	}

	protected <T extends AstNode> T findAncestor(Class<T> type) {
		return findAncestorInternal(stackInternal, type)
	}

	protected <T extends AstNode> T findAncestor(Class<T> type, int startDepth) {
		return findAncestorInternal(stackInternal.subList(startDepth, stackInternal.size()), type)
	}

	static private <T extends AstNode> T findAncestorInternal(List<AstNode> stack, Class<T> type) {
		return (T) stack.find { type.isAssignableFrom(it.class) }
	}

	@Override T beforeVisit(AstNode node) {
		stackInternal.add(0, node)
		return defaultResult()
	}

	@Override T afterVisit(AstNode node, T result) {
		stackInternal.remove(0)
		return result
	}

	@Override T visit(AstNode node) {
		return (T) node.accept(this)
	}

	@SuppressWarnings("GrMethodMayBeStatic")
	protected T defaultResult() {
		return null
	}

	@Override T aggregateResult(T aggregate, T nextResult) {
		return nextResult
	}
}
