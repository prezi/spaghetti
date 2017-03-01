package com.prezi.spaghetti.generator.test

import com.google.common.io.ByteStreams
import com.google.common.io.Files
import com.google.common.io.Resources
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.bundle.ModuleBundleFactory
import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.bundle.internal.DefaultModuleBundleSet
import com.prezi.spaghetti.bundle.internal.ModuleBundleParameters
import com.prezi.spaghetti.definition.ModuleConfiguration
import com.prezi.spaghetti.definition.ModuleDefinitionSource
import com.prezi.spaghetti.definition.internal.DefaultEntityWithModuleMetaData
import com.prezi.spaghetti.definition.internal.DefaultModuleDefinitionSource
import com.prezi.spaghetti.definition.internal.ModuleConfigurationParser
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
import com.prezi.spaghetti.packaging.internal.ExternalDependencyGenerator
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification

import java.util.zip.ZipInputStream

public abstract class LanguageSupportSpecification extends Specification {
	File rootDir
	HeaderGenerator headerGenerator
	JavaScriptBundleProcessor bundleProcessor

	static final Logger logger = LoggerFactory.getLogger(LanguageSupportSpecification)

	def setup() {
		this.rootDir = Files.createTempDir();
		this.headerGenerator = createHeaderGenerator()
		this.bundleProcessor = createBundleProcessor()
	}

	def "test harness"() {
		println "Building test module in ${rootDir}"

		when:
		// Build the dependency module
		def testDependencyDefinition = DefaultModuleDefinitionSource.fromUrl(Resources.getResource(this.class, "/DependencyModule.module"))
		def testDependencyConfig = ModuleConfigurationParser.parse(testDependencyDefinition, new DefaultModuleBundleSet([], []))
		def testDependencyModule = testDependencyConfig.localModule
		// Make the module bundle
		def testDependencyBundle = bundle(testDependencyModule.name, testDependencyModule.source.contents, Resources.getResource(this.class, "/dependency.js").text, [], [:])

		// Build the module
		def testModuleDefinition = DefaultModuleDefinitionSource.fromUrl(Resources.getResource(this.class, "/TestModule.module"))
		def moduleConfig = ModuleConfigurationParser.parse(testModuleDefinition, [new DefaultEntityWithModuleMetaData<ModuleDefinitionSource>(testDependencyDefinition, ModuleFormat.UMD)], [])
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
		def moduleBundle = bundle(module.name, module.source.contents, processedJs.text, [testDependencyModule.name], ["libWithVersion": "chai"])

		// Make the app bundle
		def testAppDefinition = DefaultModuleDefinitionSource.fromUrl(Resources.getResource(this.class, "/TestApp.module"))
		def appConfig = ModuleConfigurationParser.parse(testAppDefinition, [new DefaultEntityWithModuleMetaData<ModuleDefinitionSource>(testModuleDefinition, ModuleFormat.Wrapperless), new DefaultEntityWithModuleMetaData<ModuleDefinitionSource>(testDependencyDefinition, ModuleFormat.Wrapperless)], [])
		def appModule = appConfig.localModule

		def processedAppJs = processJavaScript(new VerbatimJavaScriptBundleProcessor("js"), appConfig, Resources.getResource(this.class, "/app.js").text)

		def appBundle = bundle(appModule.name,
				testAppDefinition.contents,
				processedAppJs,
				[module.name, testDependencyModule.name],
                [:])

		def packageDir = new File(rootDir, "package")
		// Package the application
		ApplicationPackageParameters applicationPackagingParams = new ApplicationPackageParameters(
				new DefaultModuleBundleSet([appBundle] as Set, [moduleBundle, testDependencyBundle] as Set),
				"test.js",
				appModule.name,
				true,
				[],
				[],
				["chai": "chai"]
		)
		ApplicationType.COMMON_JS.packager.packageApplicationDirectory(packageDir, applicationPackagingParams)

		def appDir = new File(rootDir, "application")
		appDir.mkdirs()
		new File(appDir, "package.json") << Resources.getResource(this.class, "/package.json").text
		new File(appDir, "npm-shrinkwrap.json") << Resources.getResource(this.class, "/npm-shrinkwrap.json").text
		executeIn(appDir, "npm", "install")

		// Merge the two node_modules folders together
		FileUtils.copyDirectory(packageDir, appDir)
		// Execute the application
		logger.info("Executing in: " + appDir);
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
		println "Executing ${args.join(" ")}"
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

	private ModuleBundle bundle(String name, String definition, String javaScript, Collection<String> moduleDependencies, Map<String, String> externalDependencies) {
		def bundleDir = new File(rootDir, "bundles/" + name)
		List<String> importedExternalDependencyVars = ExternalDependencyGenerator.getImportedVarNames(externalDependencies.keySet());
		return ModuleBundleFactory.createDirectory(bundleDir, new ModuleBundleParameters(
				name,
				definition,
				"1.0",
				ModuleFormat.UMD,
				null,
				InternalGeneratorUtils.bundleJavaScript(javaScript, importedExternalDependencyVars),
				null,
				moduleDependencies,
				externalDependencies,
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
