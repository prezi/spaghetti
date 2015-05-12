package com.prezi.spaghetti.ast;

import java.io.Serializable;
import java.util.List;

/**
 * Fully qualified name.
 */
public interface FQName extends Comparable<FQName>, Serializable {
	List<String> getParts();

	String getNamespace();

	String getLocalName();

	String getFullyQualifiedName();

	FQName qualifyLocalName(FQName name);

	boolean hasNamespace();
}
