package com.prezi.spaghetti.haxe;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Automatically prepend deprecation warnings to generated code.
 */
/*
 * Note: This is in Java, because otherwise the Groovy compiler doesn't find it.
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})
@GroovyASTTransformationClass(classes = WithDeprecationTransformation.class)
public @interface WithDeprecation {
}
