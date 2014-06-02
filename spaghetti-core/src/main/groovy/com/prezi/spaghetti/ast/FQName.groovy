package com.prezi.spaghetti.ast

import com.prezi.spaghetti.grammar.ModuleParser.QualifiedNameContext
import groovy.transform.EqualsAndHashCode
/**
 * Fully qualified name.
 */
@EqualsAndHashCode
final public class FQName implements Comparable<FQName>, Serializable {
	final String namespace
	final String localName
	final String fullyQualifiedName

	private FQName(String namespace, String localName) {
		this(makeFullyQualifiedName(namespace, localName), namespace, localName)
	}

	private static String makeFullyQualifiedName(String namespace, String localName) {
		if (namespace) {
			"${namespace}.${localName}"
		} else {
			localName
		}
	}

	private FQName(String fullyQualifiedName, String namespace, String localName) {
		this.fullyQualifiedName = fullyQualifiedName
		this.localName = localName
		this.namespace = namespace ? namespace : null
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
		return new FQName(fqName, _namespace, _name)
	}

	public static FQName fromString(String namespace, String name) {
		return new FQName(namespace, name)
	}

	public static FQName fromContext(QualifiedNameContext context) {
		def namespace = new StringBuilder()
		String localName = null
		context.Name().each {
			if (localName) {
				if (namespace.length() > 0) {
					namespace.append(".")
				}
				namespace.append(localName)
			}
			localName = it.text
		}
		return fromString(namespace.toString(), localName)
	}

	public FQName qualifyLocalName(FQName name) {
		if (name.hasNamespace()) {
			return name
		} else {
			return fromString(namespace, name.localName)
		}
	}

	public static FQName qualifyLocalName(String namespace, FQName name) {
		if (name.hasNamespace()) {
			return name
		} else {
			return fromString(namespace, name.localName)
		}
	}

	public List<String> getParts() {
		List<String> result = namespace ? namespace.tokenize(".") : []
		result.add localName
		return result
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
