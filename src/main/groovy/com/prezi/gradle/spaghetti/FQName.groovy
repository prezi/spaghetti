package com.prezi.gradle.spaghetti

import groovy.transform.EqualsAndHashCode
import org.gradle.api.Named

/**
 * Fully qualified name.
 */
@EqualsAndHashCode
final public class FQName {
	final String namespace
	final String localName

	private FQName(String namespace, String localName) {
		this.localName = localName
		this.namespace = namespace
	}

	public static FQName fromString(String fqName) {
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
		return new FQName(_namespace, _name)
	}

	public FQName resolveLocalName(String localName) {
		if (localName.indexOf('.') != -1) {
			return fromString(localName)
		} else {
			return new FQName(namespace, localName)
		}
	}

	public String getFullyQualifiedName() {
		return namespacePrefix + localName
	}

	public String getNamespacePrefix() {
		return (namespace == null ? "" : namespace + ".")
	}

	public File createNamespacePath(File root) {
		File result = root
		if (namespace != null) {
			namespace.split(/\./).each { name -> result = new File(result, name) }
		}
		return result
	}

	@Override
	String toString() {
		return fullyQualifiedName
	}
}
