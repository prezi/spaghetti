package com.prezi.spaghetti.ast.internal;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.prezi.spaghetti.ast.AstNode;
import com.prezi.spaghetti.ast.ModuleVisitor;
import com.prezi.spaghetti.ast.TypeChain;
import com.prezi.spaghetti.ast.TypeReference;
import com.prezi.spaghetti.ast.VoidTypeReference;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.Collections;
import java.util.List;

public class DefaultTypeChain extends AbstractArrayedTypeReference implements TypeChain {
	private final List<TypeReference> elements = Lists.newArrayList();

	public DefaultTypeChain(int arrayDimensions) {
		super(arrayDimensions);
	}

	@Override
	public List<TypeReference> getParameters() {
		if (elements.size() == 2 && elements.get(0).equals(VoidTypeReference.VOID)) {
			return Collections.emptyList();
		}

		return elements.subList(0, elements.size() - 1);
	}

	@Override
	public TypeReference getReturnType() {
		return DefaultGroovyMethods.last(elements);
	}

	@Override
	public Iterable<? extends AstNode> getChildren() {
		return Iterables.concat(super.getChildren(), elements);
	}

	@Override
	protected <T> T acceptInternal(ModuleVisitor<? extends T> visitor) {
		return visitor.visitTypeChain(this);
	}

	@Override
	public String toString() {
		return Joiner.on("->").join(elements);
	}

	@Override
	public List<TypeReference> getElements() {
		return elements;
	}
}
