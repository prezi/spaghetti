package com.prezi.spaghetti

import com.prezi.spaghetti.grammar.ModuleParser
import groovy.transform.EqualsAndHashCode
/**
 * Fully qualified name.
 */
@EqualsAndHashCode
final public class FQName implements Comparable<FQName> {
	final String namespace
	final String localName

	private FQName(String namespace, String localName) {
		this.localName = localName
		this.namespace = namespace?.empty ? null : namespace
	}

	public static FQName fromString(String fqName) {
		if (fqName == null) {
			throw new IllegalArgumentException("Qualified name cannot be empty")
		}
		if (fqName.empty) {
			throw new IllegalArgumentException("Qualified name cannot be empty")
		}
		String _name
		String _namespace
		int lastDot = fqName.lastIndexOf('.')
		if (lastDot == -1)
		{
			_namespace = null
			_name = fqName
		}
		else
		{
			_namespace = fqName.substring(0, lastDot)
			_name = fqName.substring(lastDot + 1)
		}
		return fromString(_namespace, _name)
	}

	public static FQName fromString(String namespace, String name) {
		return new FQName(namespace, name)
	}

	public static FQName fromContext(ModuleParser.QualifiedNameContext context) {
		return fromString(context.parts.collect() { it.text }.join("."))
	}

	public FQName qualifyLocalName(FQName name) {
		if (name.hasNamespace()) {
			return name
		} else {
			return new FQName(namespace, name.localName)
		}
	}

	public static FQName qualifyLocalName(String namespace, FQName name) {
		if (name.hasNamespace()) {
			return name
		} else {
			return new FQName(namespace, name.localName)
		}
	}

	public String getFullyQualifiedName() {
		return namespacePrefix + localName
	}

	public String getNamespacePrefix() {
		return (namespace == null ? "" : namespace + ".")
	}

	public boolean hasNamespace() {
		return namespace != null
	}

	@Override
	String toString() {
		return fullyQualifiedName
	}

	@Override
	int compareTo(FQName o) {
		return fullyQualifiedName.compareTo(o.fullyQualifiedName)
	}
}
