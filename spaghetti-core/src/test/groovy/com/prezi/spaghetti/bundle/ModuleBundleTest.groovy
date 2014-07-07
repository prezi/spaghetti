package com.prezi.spaghetti.bundle

import com.prezi.spaghetti.Version
import com.prezi.spaghetti.structure.IOAction
import com.prezi.spaghetti.structure.IOCallable
import com.prezi.spaghetti.structure.StructuredReader
import com.prezi.spaghetti.structure.StructuredWriter
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
						"3.7",
						"http://git.example.com/test",
						"console.log('hello');",
						"sourcemap",
						["com.example.alma", "com.example.bela"] as SortedSet,
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
				"Module-Dependencies: com.example.alma,com.example.bela",
				"Module-Source: http://git.example.com/test",
		].sort()
	}

	def "load bundle without any files"() {
		def source = Mock(StructuredReader)

		when:
		DefaultModuleBundle.loadInternal(source)

		then:
		1 * source.hasFile("META-INF/MANIFEST.MF") >> false
		0 * _
		def ex = thrown IllegalArgumentException
		ex.message.contains "Not a module, missing manifest"
	}

	@SuppressWarnings("GroovyAssignabilityCheck")
	def "load bundle with all files present"() {
		def source = Mock(StructuredReader)

		when:
		def bundle = DefaultModuleBundle.loadInternal(source)

		then:
		_ * source.hasFile(_) >> true
		_ * source.init()
		_ * source.close()
		//noinspection GroovyAssignabilityCheck
		1 * source.processFiles({
			StructuredReader.FileHandler handler = it
			handler.handleFile("META-INF/MANIFEST.MF", content(
					"Manifest-Version: 1.0",
					"Spaghetti-Version: 2.5",
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
		def source = Mock(StructuredReader)
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
		def source = Mock(StructuredReader)
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
		def source = Mock(StructuredReader)
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

	private static ModuleBundle fakeModule(StructuredReader source) {
		return new DefaultModuleBundle(source, "test", "3.7", null, [].toSet(), [].toSet())
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
