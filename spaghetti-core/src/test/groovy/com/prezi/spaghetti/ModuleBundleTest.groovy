package com.prezi.spaghetti

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification

/**
 * Created by lptr on 15/05/14.
 */
class ModuleBundleTest extends Specification {

	static final Logger logger = LoggerFactory.getLogger(ModuleBundleTest)

	def "simple module"() {
		def builder = Mock(ModuleBundleBuilder)
		String manifest = null
		when:
		ModuleBundle.create(
				builder,
				new ModubleBundleParameters(
						name: "test",
						definition: "definition",
						version: "3.7",
						sourceBaseUrl: "http://git.example.com/test",
						bundledJavaScript: "console.log('hello');",
						sourceMap: "sourcemap",
						dependentModules: ["com.example.alma", "com.example.bela"],
						resourcesDirectory: null)
		)

		then:
		1 * builder.init()
		1 * builder.addEntry("META-INF/MANIFEST.MF", { manifest = get(it) })
		1 * builder.addEntry("module.def", { get(it) == "definition" })
		1 * builder.addEntry("module.js", { get(it) == "console.log('hello');" })
		1 * builder.addEntry("module.map", { get(it) == "sourcemap" })
		1 * builder.create()
		1 * builder.close()
		0 * _
		manifest.tokenize("\r?\n").sort() == [
				"Manifest-Version: 1.0",
				"Spaghetti-Version: ${Version.SPAGHETTI_VERSION}",
				"Module-Name: test",
				"Module-Version: 3.7",
				"Module-Dependencies: com.example.alma,com.example.bela",
				"Module-Source: http://git.example.com/test",
		].sort()
	}

	private static String get(Closure cl) {
		def out = new ByteArrayOutputStream()
		cl(out)

		def result = out.toString("utf-8")
		logger.info "Contents:\n{}", result
		return result
	}
}
