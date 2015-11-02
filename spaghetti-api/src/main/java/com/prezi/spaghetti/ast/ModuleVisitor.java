package com.prezi.spaghetti.ast;

public interface ModuleVisitor<T> {
	T visitAnnotationNode(AnnotationNode node);
	T visitConstEntryNode(ConstEntryNode node);
	T visitConstNode(ConstNode node);
	T visitDocumentationNode(DocumentationNode node);
	T visitEnumNode(EnumNode node);
	T visitEnumReference(EnumReference reference);
	T visitEnumValueNode(EnumValueNode node);
	T visitExternInterfaceNode(ExternInterfaceNode node);
	T visitExternInterfaceReference(ExternInterfaceReference reference);
	T visitImportNode(ImportNode node);
	T visitInterfaceNode(InterfaceNode node);
	T visitInterfaceReference(InterfaceReference reference);
	T visitMethodNode(MethodNode node);
	T visitMethodParameterNode(MethodParameterNode node);
	T visitModuleNode(ModuleNode node);
	T visitPrimitiveTypeReference(PrimitiveTypeReference reference);
	T visitPropertyNode(PropertyNode node);
	T visitStructNode(StructNode node);
	T visitStructReference(StructReference reference);
	T visitFunctionType(FunctionType functionType);
	T visitTypeParameterNode(TypeParameterNode node);
	T visitTypeParameterReference(TypeParameterReference reference);
	T visitVoidTypeReference(VoidTypeReference reference);

	T visit(AstNode node);
	T beforeVisit(AstNode node);
	T afterVisit(AstNode node, T result);
	T aggregateResult(T aggregate, T nextResult);
}
