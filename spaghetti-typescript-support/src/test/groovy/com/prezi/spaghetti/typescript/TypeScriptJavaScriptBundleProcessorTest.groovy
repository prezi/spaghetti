package com.prezi.spaghetti.typescript

import com.prezi.spaghetti.ast.internal.DefaultLocation
import com.prezi.spaghetti.ast.internal.DefaultModuleNode
import com.prezi.spaghetti.bundle.DefinitionLanguage
import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.definition.internal.DefaultEntityWithModuleMetaData
import com.prezi.spaghetti.definition.internal.DefaultModuleConfiguration
import com.prezi.spaghetti.definition.internal.DefaultModuleDefinitionSource
import com.prezi.spaghetti.generator.internal.DefaultJavaScriptBundleProcessorParameters
import spock.lang.Specification

class TypeScriptJavaScriptBundleProcessorTest extends Specification {
	def "processModuleJavaScript: TypeScript accessors are sorted and don't have redudant lines"() {
		def processor = new TypeScriptJavaScriptBundleProcessor()
		def config = new DefaultModuleConfiguration(
				makeModuleNode("spaghetti.test.main"),
				makeDependencies(["spaghetti.test.dep", "com.b", "spaghetti.other.dep", "spaghetti.test.a", "com.a"]),
				makeDependencies(["com.lazy.first", "com.lazy.second"]),
				Collections.emptySet())
		def params = new DefaultJavaScriptBundleProcessorParameters(config);

		when:
		def result = processor.processModuleJavaScript(params, "/* This is the JavaScript module */");
		then:
		result == """var spaghetti_test_main=null;
var com_a=Spaghetti["dependencies"]["com.a"];
var com_b=Spaghetti["dependencies"]["com.b"];
var spaghetti_other_dep=Spaghetti["dependencies"]["spaghetti.other.dep"];
var spaghetti_test_a=Spaghetti["dependencies"]["spaghetti.test.a"];
var spaghetti_test_dep=Spaghetti["dependencies"]["spaghetti.test.dep"];
var get_com_lazy_first=Spaghetti["dependencies"]["com.lazy.first"];
var get_com_lazy_second=Spaghetti["dependencies"]["com.lazy.second"];
/* This is the JavaScript module */
return spaghetti_test_main;
"""
	}

	def "processModuleJavaScript: 'use strict' is copied to the top line"() {
		def processor = new TypeScriptJavaScriptBundleProcessor()
		def config = new DefaultModuleConfiguration(
				makeModuleNode("spaghetti.test.main"),
				makeDependencies([]),
				Collections.emptySet(),
				Collections.emptySet())
		def params = new DefaultJavaScriptBundleProcessorParameters(config);

		when:
		def result = processor.processModuleJavaScript(params, "'use strict';/* This is the JavaScript module */");
		then:
		result == """'use strict';
var spaghetti_test_main=null;
/* This is the JavaScript module */
return spaghetti_test_main;
"""
	}

	def "processModuleJavaScript: \"use strict\" with double quotes is copied to the top line"() {
		def processor = new TypeScriptJavaScriptBundleProcessor()
		def config = new DefaultModuleConfiguration(
				makeModuleNode("spaghetti.test.main"),
				makeDependencies([]),
				Collections.emptySet(),
				Collections.emptySet())
		def params = new DefaultJavaScriptBundleProcessorParameters(config);

		when:
		def result = processor.processModuleJavaScript(params, "\"use strict\";/* This is the JavaScript module */");
		then:
		result == """'use strict';
var spaghetti_test_main=null;
/* This is the JavaScript module */
return spaghetti_test_main;
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
