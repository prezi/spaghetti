package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.internal.DefaultLocation
import com.prezi.spaghetti.ast.internal.DefaultModuleNode
import com.prezi.spaghetti.bundle.DefinitionLanguage
import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.definition.internal.DefaultEntityWithModuleMetaData
import com.prezi.spaghetti.definition.internal.DefaultModuleConfiguration
import com.prezi.spaghetti.definition.internal.DefaultModuleDefinitionSource;
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.generator.JavaScriptBundleProcessorParameters
import com.prezi.spaghetti.generator.internal.DefaultJavaScriptBundleProcessorParameters

import java.util.Collections
import java.util.HashSet

import spock.lang.Specification

class TypeScriptJavaScriptBundleProcessorTest extends Specification {
	def "processModuleJavaScript: TypeScript accessors are sorted and don't have redudant lines"() {
		def processor = new TypeScriptJavaScriptBundleProcessor()
		def config = new DefaultModuleConfiguration(
			makeModuleNode("spaghetti.test.main"),
			makeDependencies(["spaghetti.test.dep", "com.b", "spaghetti.other.dep", "spaghetti.test.a", "com.a"]),
			Collections.emptySet())
		def params = new DefaultJavaScriptBundleProcessorParameters(config);

		when:
		def result = processor.processModuleJavaScript(params, "/* This is the JavaScript module */");
		then:
		result == """var com=(com||{});
com.a=Spaghetti["dependencies"]["com.a"];
com.b=Spaghetti["dependencies"]["com.b"];
var spaghetti=(spaghetti||{});
spaghetti.other=(spaghetti.other||{});
spaghetti.other.dep=Spaghetti["dependencies"]["spaghetti.other.dep"];
spaghetti.test=(spaghetti.test||{});
spaghetti.test.a=Spaghetti["dependencies"]["spaghetti.test.a"];
spaghetti.test.dep=Spaghetti["dependencies"]["spaghetti.test.dep"];
/* This is the JavaScript module */
return spaghetti.test.main;
"""
	}

	def makeModuleNode(String name) {
		def source = DefaultModuleDefinitionSource.fromStringWithLang("internal", "", DefinitionLanguage.TypeScript)
		DefaultLocation location = new DefaultLocation(source, 0, 0);
		return new DefaultModuleNode(location, name, name);
	}

	def makeDependencies(List<String> names) {
		def nodes = names.collect { name ->
			new DefaultEntityWithModuleMetaData(makeModuleNode(name), ModuleFormat.UMD)
		}
		return new HashSet(nodes)
	}
}
