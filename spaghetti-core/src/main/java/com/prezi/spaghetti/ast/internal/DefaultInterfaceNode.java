package com.prezi.spaghetti.ast.internal;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.AnnotationNode;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.DocumentationNode;
import com.prezi.spaghetti.ast.FQName;
import com.prezi.spaghetti.ast.InterfaceNode;
import com.prezi.spaghetti.ast.InterfaceNodeBase;
import com.prezi.spaghetti.ast.InterfaceReference;
import com.prezi.spaghetti.ast.InterfaceReferenceBase;
import com.prezi.spaghetti.ast.Location;
import com.prezi.spaghetti.ast.MethodNode;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeParameterNode;
import com.prezi.spaghetti.ast.TypeReference;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultInterfaceNode extends AbstractParametrizedTypeNode implements InterfaceNode, AnnotatedNodeInternal, DocumentedNodeInternal {
	private final NamedNodeSetInternal<AnnotationNode> annotations = NodeSets.newNamedNodeSet("annotation");
	private DocumentationNode documentation = DocumentationNode.NONE;
	private final QualifiedTypeNodeReferenceSetInternal<InterfaceReferenceBase<? extends InterfaceNodeBase>> superInterfaces = NodeSets.newQualifiedNodeReferenceSet("super interface");
	private final NamedNodeSetInternal<MethodNode> methods = NodeSets.newNamedNodeSet("method");

	public DefaultInterfaceNode(Location location, FQName qualifiedName) {
		super(location, qualifiedName);
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), superInterfaces, methods);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitInterfaceNode(this);
	}

	@Override
	public NamedNodeSetInternal<AnnotationNode> getAnnotations() {
		return annotations;
	}

	@Override
	public DocumentationNode getDocumentation() {
		return documentation;
	}

	@Override
	public void setDocumentation(DocumentationNode documentation) {
		this.documentation = documentation;
	}

	@Override
	public QualifiedTypeNodeReferenceSetInternal<InterfaceReferenceBase<? extends InterfaceNodeBase>> getSuperInterfaces() {
		return superInterfaces;
	}

	@Override
	public NamedNodeSetInternal<MethodNode> getMethods() {
		return methods;
	}

	@Override
	public NamedNodeSetInternal<MethodNode> getAllMethods() {
		NamedNodeSetInternal<MethodNode> methodNodes = NodeSets.newNamedNodeSet("method");
		resolveMethodsWithTypeParameters(this, Collections.<TypeParameterNode, TypeReference>emptyMap(), methodNodes, Sets.<String>newHashSet());
		return methodNodes;
	}

	public static void resolveMethodsWithTypeParameters(InterfaceNode interfaceNode, Map<TypeParameterNode, TypeReference> bindings, NamedNodeSetInternal<MethodNode> methodDefinitions, Set<String> methodsGenerated) {
		for (MethodNode methodNode : interfaceNode.getMethods()) {
			if (!methodsGenerated.add(methodNode.getName())) {
				continue;
			}
			MethodNode resolvedMethod = DefaultMethodNode.resolveWithTypeParameters(methodNode, bindings);
			methodDefinitions.addInternal(resolvedMethod);
		}

		for (InterfaceReferenceBase<? extends InterfaceNodeBase> superIfaceRef : interfaceNode.getSuperInterfaces()) {
			Map<TypeParameterNode, TypeReference> superBindings = Maps.newLinkedHashMap(bindings);
			if (superIfaceRef instanceof InterfaceReference) {
				InterfaceNode superIface = ((InterfaceReference) superIfaceRef).getType();
				List<TypeParameterNode> typeParameters = Lists.newArrayList(superIface.getTypeParameters());
				for (int i = 0; i < typeParameters.size(); i++) {
					TypeParameterNode param = typeParameters.get(i);
					TypeReference ref = superIfaceRef.getArguments().get(i);
					superBindings.put(param, ref);
				}
				resolveMethodsWithTypeParameters(superIface, superBindings, methodDefinitions, methodsGenerated);
			}
		}
	}
}
