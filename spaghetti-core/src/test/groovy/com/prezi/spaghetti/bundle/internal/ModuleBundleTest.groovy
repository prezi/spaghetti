package com.prezi.spaghetti.bundle.internal

import com.google.common.collect.ImmutableSortedMap
import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.bundle.DefinitionLanguage
import com.prezi.spaghetti.internal.Version
import com.prezi.spaghetti.structure.internal.FileProcessor
import com.prezi.spaghetti.structure.internal.IOAction
import com.prezi.spaghetti.structure.internal.IOCallable
import com.prezi.spaghetti.structure.internal.StructuredProcessor
import com.prezi.spaghetti.structure.internal.StructuredWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification

class ModuleBundleTest extends Specification {

	static final Logger logger = LoggerFactory.getLogger(ModuleBundleTest)

	def "create bundle"() {
		def builder = Mock(StructuredWriter)
		String manifest = null
		when:
		DefaultModuleBundle.create(
				builder,
				new ModuleBundleParameters(
						"test",
						"definition",
						DefinitionLanguage.Spaghetti,
						"3.7",
						ModuleFormat.Wrapperless,
						"http://git.example.com/test",
						"console.log('hello');",
						"sourcemap",
						["com.example.alma", "com.example.bela"],
						ImmutableSortedMap.copyOf("React": "react", "\$": "jquery"),
						null)
		)

		then:
		1 * builder.init()
		1 * builder.appendFile("META-INF/MANIFEST.MF", { manifest = get(it) })
		1 * builder.appendFile("module.def", { it == "definition" })
		1 * builder.appendFile("module.js", { it == "console.log('hello');" })
		1 * builder.appendFile("module.map", { it == "sourcemap" })
		1 * builder.create()
		1 * builder.close()
		0 * _
		manifest.tokenize("\r?\n").sort() == [
				"Manifest-Version: 1.0",
				"Spaghetti-Version: ${Version.SPAGHETTI_VERSION}",
				"Module-Name: test",
				"Module-Version: 3.7",
				"Module-Format: Wrapperless",
				"Definition-Language: Spaghetti",
				"Module-Dependencies: com.example.alma,com.example.bela",
				"External-Dependencies: \$:jquery,React:react",
				"Module-Source: http://git.example.com/test",
		].sort()
	}

	def "load bundle without any files"() {
		def source = Mock(StructuredProcessor)

		when:
		DefaultModuleBundle.loadInternal(source)

		then:
		1 * source.hasFile("META-INF/MANIFEST.MF") >> false
		0 * _
		def ex = thrown IllegalArgumentException
		ex.message.contains "Not a module, missing manifest"
	}

	@SuppressWarnings("GroovyAssignabilityCheck")
	def "load bundle with empty dependency lists"() {
		def source = Mock(StructuredProcessor)

		when:
		def bundle = DefaultModuleBundle.loadInternal(source)

		then:
		_ * source.hasFile(_) >> true
		_ * source.init()
		_ * source.close()
		//noinspection GroovyAssignabilityCheck
		1 * source.processFiles({
			FileProcessor handler = it
			handler.processFile("META-INF/MANIFEST.MF", content(
					"Manifest-Version: 1.0",
					"Spaghetti-Version: 3.5",
					"Module-Name: com.example.test",
					"Module-Version: 3.7",
					"External-Dependencies: ",
					"Module-Dependencies: ",
					"Module-Source: http://git.example.com/test",
					"" // Must have newline at end of manifest
			))
			true
		})
		bundle.dependentModules == ([] as SortedSet)
		bundle.externalDependencies == [:]
		0 * _
	}

	@SuppressWarnings("GroovyAssignabilityCheck")
	def "load bundle with all files present"() {
		def source = Mock(StructuredProcessor)

		when:
		def bundle = DefaultModuleBundle.loadInternal(source)

		then:
		_ * source.hasFile(_) >> true
		_ * source.init()
		_ * source.close()
		//noinspection GroovyAssignabilityCheck
		1 * source.processFiles({
			FileProcessor handler = it
			handler.processFile("META-INF/MANIFEST.MF", content(
					"Manifest-Version: 1.0",
					"Spaghetti-Version: 3.5",
					"Module-Name: com.example.test",
					"Module-Version: 3.7",
					"External-Dependencies: React:react,\$:jquery,shorthand",
					"Module-Dependencies: com.example.alma,com.example.bela",
					"Module-Source: http://git.example.com/test",
					"" // Must have newline at end of manifest
			))
			true
		})
		bundle.name == "com.example.test"
		bundle.version == "3.7"
		bundle.sourceBaseUrl == "http://git.example.com/test"
		bundle.dependentModules == (["com.example.alma", "com.example.bela"] as SortedSet)
		bundle.externalDependencies == ["\$": "jquery", "React": "react", "shorthand": "shorthand"]
		0 * _
	}

	def "definition from module"() {
		def source = Mock(StructuredProcessor)
		def bundle = fakeModule(source)
		when:
		def definition = bundle.definition

		then:
		1 * source.init()
		1 * source.hasFile("module.def") >> true
		//noinspection GroovyAssignabilityCheck
		_ * source.processFile("module.def", { it.processFile("module.def", content(
				"module com.example.test as Test"
		)); true })
		1 * source.close()
		0 * _
		definition == "module com.example.test as Test"
	}

	def "javascript from module"() {
		def source = Mock(StructuredProcessor)
		def bundle = fakeModule(source)
		when:
		def javaScript = bundle.javaScript

		then:
		1 * source.init()
		1 * source.hasFile("module.js") >> true
		//noinspection GroovyAssignabilityCheck
		1 * source.processFile("module.js", { it.processFile("module.js", content(
				"console.log('hello');"
		)); true })
		1 * source.close()
		0 * _
		javaScript == "console.log('hello');"
	}

	def "source map from module"() {
		def source = Mock(StructuredProcessor)
		def bundle = fakeModule(source)
		when:
		def sourceMap = bundle.sourceMap

		then:
		1 * source.init()
		1 * source.hasFile("module.map") >> true
		//noinspection GroovyAssignabilityCheck
		1 * source.processFile("module.map", { it.processFile("module.map", content(
				"sourcemap"
		)); true })
		1 * source.close()
		0 * _
		sourceMap == "sourcemap"
	}

	private static ModuleBundle fakeModule(StructuredProcessor source) {
		return new DefaultModuleBundle(source, "test", "3.7", ModuleFormat.UMD, DefinitionLanguage definitionLang, null, [] as Set, [:], [] as Set)
	}

	private static String get(IOAction<OutputStream> action) {
		def out = new ByteArrayOutputStream()
		action.execute(out)

		def result = out.toString("utf-8")
		logger.info "Reading:\n{}", result
		return result
	}

	private static IOCallable<InputStream> content(String... lines) {
		def data = lines.join("\n")
		logger.info "Writing:\n${data}"
		return new IOCallable<InputStream>() {
			@Override
			InputStream call() throws IOException {
				return new ByteArrayInputStream(data.getBytes("utf-8"))
			}
		}
	}
}
