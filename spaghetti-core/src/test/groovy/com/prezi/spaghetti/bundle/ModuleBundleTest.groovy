package com.prezi.spaghetti.bundle

import com.prezi.spaghetti.Version
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification

/**
 * Created by lptr on 15/05/14.
 */
class ModuleBundleTest extends Specification {

	static final Logger logger = LoggerFactory.getLogger(ModuleBundleTest)

	def "create bundle"() {
		def builder = Mock(ModuleBundleBuilder)
		String manifest = null
		when:
		ModuleBundle.create(
				builder,
				new ModuleBundleParameters(
						name: "test",
						definition: "definition",
						version: "3.7",
						sourceBaseUrl: "http://git.example.com/test",
						javaScript: "console.log('hello');",
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

	def "load bundle without any files"() {
		def source = Mock(ModuleBundleSource)

		when:
		ModuleBundle.loadInternal(source)

		then:
		1 * source.processFiles(_)
		0 * _
		IllegalArgumentException ex = thrown()
		ex.message.contains "Not a module, missing manifest"
	}

	def "load bundle with manifest"() {
		def source = Mock(ModuleBundleSource)

		when:
		def bundle = ModuleBundle.loadInternal(source)

		then:
		//noinspection GroovyAssignabilityCheck
		1 * source.processFiles({
			ModuleBundleSource.ModuleBundleFileHandler handler = it
			handler.handleFile("META-INF/MANIFEST.MF", content(
					"Manifest-Version: 1.0",
					"Spaghetti-Version: 1.5",
					"Module-Name: com.example.test",
					"Module-Version: 3.7",
					"Module-Dependencies: com.example.alma,com.example.bela",
					"Module-Source: http://git.example.com/test",
					"" // Must have newline at end of manifest
			))
			handler.handleFile("module.def", content(
					"module com.example.test as Test"
			))
			handler.handleFile("module.js", content(
					"console.log('hello');"
			))
			handler.handleFile("module.map", content(
					"sourcemap"
			))
			true
		})
		0 * _
		bundle.name == "com.example.test"
		bundle.version == "3.7"
		bundle.definition == "module com.example.test as Test"
		bundle.sourceBaseUrl == "http://git.example.com/test"
		bundle.bundledJavaScript == "console.log('hello');"
		bundle.sourceMap == "sourcemap"
		bundle.dependentModules.sort() == ["com.example.alma", "com.example.bela"]
	}

	private static String get(Closure cl) {
		def out = new ByteArrayOutputStream()
		cl(out)

		def result = out.toString("utf-8")
		logger.info "Reading:\n{}", result
		return result
	}

	private static Closure<InputStream> content(String... lines) {
		def data = lines.join("\n")
		logger.info "Writing:\n${data}"
		return {
			new ByteArrayInputStream(data.getBytes("utf-8"))
		}
	}
}
