package com.prezi.spaghetti.obfuscation

import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.config.ModuleConfiguration
import groovy.transform.TupleConstructor

/**
 * Created by lptr on 16/05/14.
 */
@TupleConstructor
public class ObfuscationParameters {
	ModuleConfiguration config
	ModuleNode module
	String javaScript
	String sourceMap
	URI sourceMapRoot
	String nodeSourceMapRoot
	Set<File> closureExterns
	Set<String> additionalSymbols
	File workingDirectory
}
