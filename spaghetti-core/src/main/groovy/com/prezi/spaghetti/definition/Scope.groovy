package com.prezi.spaghetti.definition
/**
 * Created by lptr on 23/11/13.
 */
public interface Scope {
	FQName resolveName(FQName name)
	FQName resolveExtern(FQName name)
}
