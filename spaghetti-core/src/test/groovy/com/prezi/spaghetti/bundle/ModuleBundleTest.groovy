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
		1 * source.hasFile("META-INF/MANIFEST.MF") >> false
		0 * _
		IllegalArgumentException ex = thrown()
		ex.message.contains "Not a module, missing manifest"
	}

	@SuppressWarnings("GroovyAssignabilityCheck")
	def "load bundle with all files present"() {
		def source = Mock(ModuleBundleSource)

		when:
		def bundle = ModuleBundle.loadInternal(source)

		then:
		_ * source.hasFile(_) >> true
		_ * source.init()
		_ * source.close()
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
			true
		})
		bundle.name == "com.example.test"
		bundle.version == "3.7"
		bundle.sourceBaseUrl == "http://git.example.com/test"
		bundle.dependentModules.sort() == ["com.example.alma", "com.example.bela"]
		0 * _
	}

	def "definition from module"() {
		def source = Mock(ModuleBundleSource)
		def bundle = fakeModule(source)
		when:
		def definition = bundle.definition

		then:
		1 * source.init()
		1 * source.hasFile("module.def") >> true
		//noinspection GroovyAssignabilityCheck
		_ * source.processFile("module.def", { it.handleFile("module.def", content(
				"module com.example.test as Test"
		)); true })
		1 * source.close()
		0 * _
		definition == "module com.example.test as Test"
	}

	def "javascript from module"() {
		def source = Mock(ModuleBundleSource)
		def bundle = fakeModule(source)
		when:
		def javaScript = bundle.javaScript

		then:
		1 * source.init()
		1 * source.hasFile("module.js") >> true
		//noinspection GroovyAssignabilityCheck
		1 * source.processFile("module.js", { it.handleFile("module.js", content(
				"console.log('hello');"
		)); true })
		1 * source.close()
		0 * _
		javaScript == "console.log('hello');"
	}

	def "source map from module"() {
		def source = Mock(ModuleBundleSource)
		def bundle = fakeModule(source)
		when:
		def sourceMap = bundle.sourceMap

		then:
		1 * source.init()
		1 * source.hasFile("module.map") >> true
		//noinspection GroovyAssignabilityCheck
		1 * source.processFile("module.map", { it.handleFile("module.map", content(
				"sourcemap"
		)); true })
		1 * source.close()
		0 * _
		sourceMap == "sourcemap"
	}

	private static ModuleBundle fakeModule(ModuleBundleSource source) {
		return new ModuleBundle(source, "test", "3.7", null, [].toSet(), [].toSet())
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
