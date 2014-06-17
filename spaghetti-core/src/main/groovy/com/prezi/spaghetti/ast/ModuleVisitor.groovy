package com.prezi.spaghetti.ast

interface ModuleVisitor<T> {
	T visitAnnotationNode(AnnotationNode node)
	T visitConstEntryNode(ConstEntryNode node)
	T visitConstNode(ConstNode node)
	T visitDocumentationNode(DocumentationNode node)
	T visitEnumNode(EnumNode node)
	T visitEnumReference(EnumReference reference)
	T visitEnumValueNode(EnumValueNode node)
	T visitExternNode(ExternNode node)
	T visitExternReference(ExternReference reference)
	T visitImportNode(ImportNode node)
	T visitInterfaceMethodNode(InterfaceMethodNode node)
	T visitInterfaceNode(InterfaceNode node)
	T visitInterfaceReference(InterfaceReference reference)
	T visitMethodParameterNode(MethodParameterNode node)
	T visitModuleMethodNode(ModuleMethodNode node)
	T visitModuleNode(ModuleNode node)
	T visitPrimitiveTypeReference(PrimitiveTypeReference reference)
	T visitPropertyNode(PropertyNode node)
	T visitStructNode(StructNode node)
	T visitStructReference(StructReference reference)
	T visitTypeChain(TypeChain chain)
	T visitTypeParameterNode(TypeParameterNode node)
	T visitTypeParameterReference(TypeParameterReference reference)
	T visitVoidTypeReference(VoidTypeReference reference)
	T visit(AstNode node)

	T beforeVisit(AstNode node)
	T afterVisit(AstNode node, T result)

	T aggregateResult(T aggregate, T nextResult)
}
