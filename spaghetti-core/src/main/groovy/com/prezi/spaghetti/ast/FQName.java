package com.prezi.spaghetti.ast;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.prezi.spaghetti.grammar.ModuleParser;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.Serializable;
import java.util.List;

/**
 * Fully qualified name.
 */
public final class FQName implements Comparable<FQName>, Serializable {
	public final String namespace;
	public final String localName;
	public final String fullyQualifiedName;

	private FQName(String namespace, String localName) {
		this(makeFullyQualifiedName(namespace, localName), namespace, localName);
	}

	private static String makeFullyQualifiedName(final String namespace, final String localName) {
		if (!Strings.isNullOrEmpty(namespace)) {
			return namespace + "." + localName;
		} else {
			return localName;
		}
	}

	private FQName(String fullyQualifiedName, String namespace, String localName) {
		this.fullyQualifiedName = fullyQualifiedName;
		this.localName = localName;
		this.namespace = !Strings.isNullOrEmpty(namespace) ? namespace : null;
	}

	public static FQName fromString(String fqName) {
		if (fqName == null) {
			throw new IllegalArgumentException("Qualified name cannot be empty");
		}

		if (fqName.isEmpty()) {
			throw new IllegalArgumentException("Qualified name cannot be empty");
		}

		String _name;
		String _namespace;
		int lastDot = fqName.lastIndexOf(".");
		if (lastDot == -1) {
			_namespace = null;
			_name = fqName;
		} else {
			_namespace = fqName.substring(0, lastDot);
			_name = fqName.substring(lastDot + 1);
		}

		return new FQName(fqName, _namespace, _name);
	}

	public static FQName fromString(String namespace, String name) {
		return new FQName(namespace, name);
	}

	public static FQName fromContext(ModuleParser.QualifiedNameContext context) {
		StringBuilder namespace = new StringBuilder();
		String localName = null;
		for (TerminalNode it : context.Name()) {
			if (!Strings.isNullOrEmpty(localName)) {
				if (namespace.length() > 0) {
					namespace.append(".");
				}

				namespace.append(localName);
			}

			localName = it.getText();
		}
		return fromString(namespace.toString(), localName);
	}

	public FQName qualifyLocalName(FQName name) {
		if (name.hasNamespace()) {
			return name;
		} else {
			return fromString(namespace, name.localName);
		}
	}

	public static FQName qualifyLocalName(String namespace, FQName name) {
		if (name.hasNamespace()) {
			return name;
		} else {
			return fromString(namespace, name.localName);
		}
	}

	public List<String> getParts() {
		List<String> result = !Strings.isNullOrEmpty(namespace) ? Lists.newArrayList(namespace.split("\\.")) : Lists.<String> newArrayList();
		result.add(localName);
		return result;
	}

	public boolean hasNamespace() {
		return namespace != null;
	}

	@Override
	public String toString() {
		return fullyQualifiedName;
	}

	@Override
	public int compareTo(FQName o) {
		return fullyQualifiedName.compareTo(o.fullyQualifiedName);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FQName fqName = (FQName) o;

		return fullyQualifiedName.equals(fqName.fullyQualifiedName);
	}

	@Override
	public int hashCode() {
		return fullyQualifiedName.hashCode();
	}
}
