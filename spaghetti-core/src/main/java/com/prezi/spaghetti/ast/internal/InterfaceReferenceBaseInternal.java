package com.prezi.spaghetti.ast.internal;

import com.prezi.spaghetti.ast.InterfaceNodeBase;
import com.prezi.spaghetti.ast.InterfaceReferenceBase;

public interface InterfaceReferenceBaseInternal<T extends InterfaceNodeBase> extends InterfaceReferenceBase<T>, ParametrizedTypeNodeReferenceInternal<T> {
}
