package com.prezi.spaghetti.obfuscation

import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.definition.ModuleDefinition
import groovy.transform.TupleConstructor

/**
 * Created by lptr on 16/05/14.
 */
@TupleConstructor
public class ObfuscationParameters {
	ModuleConfiguration config
	ModuleDefinition module
	String javaScript
	String sourceMap
	URI sourceMapRoot
	String nodeSourceMapRoot
	Set<File> closureExterns
	Set<String> additionalSymbols
	File workingDirectory
}
