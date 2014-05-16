package com.prezi.spaghetti.definition

/**
 * Created by lptr on 16/05/14.
 */
class GlobalScope implements Scope {

	private final Set<FQName> names = []
	private final Map<FQName, FQName> externs

	GlobalScope(Map<FQName, FQName> externs) {
		this.externs = externs
	}

	@Override
	FQName resolveName(FQName name) {
		if (!names.contains(name)) {
			throw new IllegalStateException("Name not found: ${name}, names registered: ${names}")
		}
		return name
	}

	@Override
	FQName resolveExtern(FQName name) {
		if (externs.containsKey(name)) {
			return externs.get(name)
		} else {
			// If we have no mapping for the extern, use the defined name
			return name
		}
	}

	void registerNames(Collection<FQName> names) {
		names.each {
			if (this.names.contains(it)) {
				throw new IllegalStateException("Global name registered multiple times: ${it}")
			}
		}
		this.names.addAll(names)
	}
}
