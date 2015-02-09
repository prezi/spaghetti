package com.prezi.spaghetti.generator.test

import com.google.common.io.ByteStreams
import com.google.common.io.Files
import com.google.common.io.Resources
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.bundle.ModuleBundleFactory
import com.prezi.spaghetti.bundle.ModuleBundleParameters
import com.prezi.spaghetti.bundle.ModuleBundleSet
import com.prezi.spaghetti.bundle.internal.DefaultModuleBundleSet
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.definition.ModuleConfigurationParser
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.generator.GeneratorParameters
import com.prezi.spaghetti.generator.HeaderGenerator
import com.prezi.spaghetti.generator.JavaScriptBundleProcessor
import com.prezi.spaghetti.generator.JavaScriptBundleProcessorParameters
import com.prezi.spaghetti.generator.VerbatimJavaScriptBundleProcessor
import com.prezi.spaghetti.generator.internal.DefaultGeneratorParameters
import com.prezi.spaghetti.generator.internal.DefaultJavaScriptBundleProcessorParameters
import com.prezi.spaghetti.generator.internal.InternalGeneratorUtils
import com.prezi.spaghetti.packaging.ApplicationPackageParameters
import com.prezi.spaghetti.packaging.ApplicationType
import org.apache.commons.io.FileUtils
import spock.lang.Specification

import java.util.zip.ZipInputStream

public abstract class LanguageSupportSpecification extends Specification {
	File rootDir
	HeaderGenerator headerGenerator
	JavaScriptBundleProcessor bundleProcessor

	def setup() {
		this.rootDir = Files.createTempDir();
		this.headerGenerator = createHeaderGenerator()
		this.bundleProcessor = createBundleProcessor()
	}

	def "test harness"() {
		println "Building test module in ${rootDir}"

		when:
		// Build the dependency module
		def testDependencyDefinition = ModuleDefinitionSource.fromUrl(Resources.getResource(this.class, "/TestDependencyModule.module"))
		def testDependencyConfig = ModuleConfigurationParser.parse(testDependencyDefinition, ModuleBundleSet.EMPTY)
		def testDependencyModule = testDependencyConfig.localModule
		// Make the module bundle
		def testDependencyBundle = bundle(testDependencyModule.name, testDependencyModule.source.contents, Resources.getResource(this.class, "/dependency.js").text)

		// Build the module
		def testModuleDefinition = ModuleDefinitionSource.fromUrl(Resources.getResource(this.class, "/TestModule.module"))
		def moduleConfig = ModuleConfigurationParser.parse(testModuleDefinition, [testDependencyDefinition], [])
		def module = moduleConfig.localModule
		GeneratorParameters generatorParameters = new DefaultGeneratorParameters(moduleConfig, "Integration test")
		def headersDir = new File(rootDir, "headers")
		FileUtils.forceMkdir(headersDir)
		headerGenerator.generateHeaders(generatorParameters, headersDir)

		def sourcesDir = new File(rootDir, "sources")
		extractSources(sourcesDir)

		def compiledJs = new File(rootDir, "compiled.js")
		compile(module, compiledJs, headersDir, sourcesDir)

		def processedJs = new File(rootDir, "processed.js")
		processedJs << processJavaScript(bundleProcessor, moduleConfig, compiledJs.text)

		// Make the module bundle
		def moduleBundle = bundle(module.name, module.source.contents, processedJs.text, testDependencyModule.name)

		// Make the app bundle
		def testAppDefinition = ModuleDefinitionSource.fromUrl(Resources.getResource(this.class, "/TestApp.module"))
		def appConfig = ModuleConfigurationParser.parse(testAppDefinition, [testModuleDefinition], [testDependencyDefinition])
		def appModule = appConfig.localModule

		def processedAppJs = processJavaScript(new VerbatimJavaScriptBundleProcessor("js"), appConfig, Resources.getResource(this.class, "/app.js").text)

		def appBundle = bundle(appModule.name,
				testAppDefinition.contents,
				processedAppJs,
				module.name)

		// Package the application
		def appDir = new File(rootDir, "application")
		ApplicationPackageParameters applicationPackagingParams = new ApplicationPackageParameters(
				new DefaultModuleBundleSet([appBundle] as Set, [moduleBundle, testDependencyBundle] as Set),
				"test.js",
				appModule.name,
				true,
				[],
				[]
		)
		ApplicationType.COMMON_JS.packager.packageApplicationDirectory(appDir, applicationPackagingParams)

		// Execute the application
		new File(appDir, "package.json") << Resources.getResource(this.class, "/package.json").text
		executeIn(appDir, "npm", "install")
		executeIn(appDir, "node_modules/.bin/mocha")

		then:
		1 == 1
	}

	public static void execute(Object... args) {
		executeIn(null, Arrays.asList(args))
	}

	public static void execute(List<?> args) {
		executeIn(null, args)
	}

	public static void executeIn(File dir, Object... args) {
		executeIn(dir, Arrays.asList(args))
	}

	public static void executeIn(File dir, List<?> args) {
		def process = args.execute((String[])null, dir)
		process.waitForProcessOutput((OutputStream) System.out, System.err)
		if (process.exitValue() != 0) {
			throw new RuntimeException("Executing ${args.join(" ")} returned error code: ${process.exitValue()}")
		}
	}

	private
	static String processJavaScript(JavaScriptBundleProcessor processor, ModuleConfiguration config, String javaScript) {
		JavaScriptBundleProcessorParameters bundleProcessorParams = new DefaultJavaScriptBundleProcessorParameters(config)
		return processor.processModuleJavaScript(bundleProcessorParams, javaScript)
	}

	private ModuleBundle bundle(String name, String definition, String javaScript, String... dependentModules) {
		def bundleDir = new File(rootDir, "bundles/" + name)
		return ModuleBundleFactory.createDirectory(bundleDir, new ModuleBundleParameters(
				name,
				definition,
				"1.0",
				null,
				InternalGeneratorUtils.bundleJavaScript(javaScript),
				null,
				dependentModules as SortedSet,
				null
		));
	}

	protected void extractSources(File sourcesDir) {
		def zipInput = new ZipInputStream(Resources.asByteSource(Resources.getResource(this.class, "/integration-test-sources.zip")).openStream())
		try {
			while (zipInput.available() > 0) {
				def entry = zipInput.nextEntry
				def outputFile = new File(sourcesDir, entry.name)
				FileUtils.forceMkdir(outputFile.parentFile)
				if (entry.directory) {
					continue
				}
				FileUtils.deleteQuietly(outputFile)
				outputFile.withOutputStream { ByteStreams.copy(zipInput, it) }
			}
		} finally {
			zipInput.close()
		}
	}

	abstract protected void compile(ModuleNode module, File outputFile, File headersDir, File sourceDir)

	abstract protected HeaderGenerator createHeaderGenerator()

	abstract protected JavaScriptBundleProcessor createBundleProcessor()
}
