package com.prezi.spaghetti.definition

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Automatically prepend JavaDoc to generated code.
 */

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
@GroovyASTTransformationClass(classes = [ WithJavaDocTransformation ])
public @interface WithJavaDoc {
}

