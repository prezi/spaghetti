package com.prezi.spaghetti.ast

/**
 * Created by lptr on 30/05/14.
 */
interface AnnotationNode extends NamedNode {
	public static final String DEFAULT_PARAMETER = "default"

	Map<String, Object> getParameters()
	boolean hasDefaultParameter()
	Object getDefaultParameter()
	boolean hasParameter(String parameter)
	Object getParameter(String parameter)
}
