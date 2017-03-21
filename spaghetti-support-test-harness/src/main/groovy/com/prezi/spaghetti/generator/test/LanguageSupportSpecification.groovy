package com.prezi.spaghetti.generator.test

import com.google.common.io.ByteStreams
import com.google.common.io.Files
import com.google.common.io.Resources
import com.prezi.spaghetti.ast.internal.parser.AstParserException
import com.prezi.spaghetti.ast.ModuleNode
import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.bundle.ModuleBundleFactory
import com.prezi.spaghetti.bundle.ModuleFormat
import com.prezi.spaghetti.bundle.DefinitionLanguage
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
import org.apache.commons.io.FilenameUtils
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

	def "test spaghetti application"() {
		println "Building test module in ${rootDir}"

		when:
		setupAndRunSpaghettiApplication("/DependencyModule.module", "/TestModule.module", [])

		then:
		1 == 1
	}

	def "test spaghetti application mixed with one typescript module definition"() {
		when:
		if (isTypeScriptDefinitionSupported()) {
			setupAndRunMixedTypeScriptDtsApp()
		}

		then:
		1 == 1
	}

	def "test unsupported typescript module definition throws exception"() {
		when:
		if (!isTypeScriptDefinitionSupported()) {
			setupAndRunMixedTypeScriptDtsApp()
		} else {
			throw new RuntimeException("because it uses a TypeScript module definition")
		}

		then:
		def e = thrown(RuntimeException)
		e.message.contains("because it uses a TypeScript module definition")
	}

	def "test spaghetti application with all typescript module definitions"() {
		when:
		if (isTypeScriptDefinitionSupported()) {
			// A spaghetti app,
			// which depends on a TypeScript module definition,
			// which depends on a TypeScript module definition.
			setupAndRunSpaghettiApplication(
				"/DependencyModule.module.d.ts",
				"/TestModule.module.d.ts",
				[ Resources.getResource(this.class, "/TestModule.module.ts") ])
		}

		then:
		1 == 1
	}

	def "test app with spaghetti def depending on typescript def throws exception"() {
		when:
		if (isTypeScriptDefinitionSupported()) {
			// A spaghetti app,
			// which depends on a Spaghetti module definition,
			// which depends on a TypeScript module definition.
			setupAndRunSpaghettiApplication(
				"/DependencyModule.module.d.ts",
				"/TestModule.module",
				[ Resources.getResource(this.class, "/TestModule.module.ts") ])
		} else {
			throw new AstParserException(DefaultModuleDefinitionSource.fromString("", ""), "");
		}

		then:
		thrown(AstParserException)
	}

	private void setupAndRunMixedTypeScriptDtsApp() {
		// A spaghetti app,
		// which depends on a TypeScript module definition,
		// which depends on a Spaghetti module definition.
		setupAndRunSpaghettiApplication(
			"/DependencyModule.module",
			"/TestModule.module.d.ts",
			[ Resources.getResource(this.class, "/TestModule.module.ts") ])
	}

	private void setupAndRunSpaghettiApplication(
			String dependencyModuleDefResourcePath,
			String testModuleDefResourcePath,
			List<URL> extraTestSources) {
		// Build the dependency module
		def testDependencyDefinition = DefaultModuleDefinitionSource.fromUrl(Resources.getResource(this.class, dependencyModuleDefResourcePath))
		def testDependencyConfig = ModuleConfigurationParser.parse(testDependencyDefinition, new DefaultModuleBundleSet([], []))
		def testDependencyModule = testDependencyConfig.localModule
		// Make the module bundle
		def testDependencyBundle = bundle(testDependencyModule, Resources.getResource(this.class, "/dependency.js").text, [], [:])

		// Build the module
		def testModuleDefinition = DefaultModuleDefinitionSource.fromUrl(Resources.getResource(this.class, testModuleDefResourcePath))
		def moduleConfig = ModuleConfigurationParser.parse(testModuleDefinition, [new DefaultEntityWithModuleMetaData<ModuleDefinitionSource>(testDependencyDefinition, ModuleFormat.UMD)], [])
		def module = moduleConfig.localModule
		GeneratorParameters generatorParameters = new DefaultGeneratorParameters(moduleConfig, "Integration test")
		def headersDir = new File(rootDir, "headers")
		FileUtils.forceMkdir(headersDir)
		headerGenerator.generateHeaders(generatorParameters, headersDir)

		def sourcesDir = new File(rootDir, "sources")
		extractSources(sourcesDir)
		for (URL url: extraTestSources) {
			FileUtils.copyURLToFile(url, new File(sourcesDir, FilenameUtils.getName(url.getPath())))
		}

		def compiledJs = new File(rootDir, "compiled.js")
		compile(module, compiledJs, headersDir, sourcesDir)

		def processedJs = new File(rootDir, "processed.js")
		processedJs << processJavaScript(bundleProcessor, moduleConfig, compiledJs.text)

		// Make the module bundle
		def moduleBundle = bundle(module, processedJs.text, [testDependencyModule.name], ["libWithVersion": "chai"])

		// Make the app bundle
		def testAppDefinition = DefaultModuleDefinitionSource.fromUrl(Resources.getResource(this.class, "/TestApp.module"))
		def appConfig = ModuleConfigurationParser.parse(testAppDefinition, [new DefaultEntityWithModuleMetaData<ModuleDefinitionSource>(testModuleDefinition, ModuleFormat.Wrapperless), new DefaultEntityWithModuleMetaData<ModuleDefinitionSource>(testDependencyDefinition, ModuleFormat.Wrapperless)], [])
		def appModule = appConfig.localModule

		def processedAppJs = processJavaScript(new VerbatimJavaScriptBundleProcessor("js"), appConfig, Resources.getResource(this.class, "/app.js").text)

		def appBundle = bundle(appModule,
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

	private ModuleBundle bundle(ModuleNode module, String javaScript, Collection<String> moduleDependencies, Map<String, String> externalDependencies) {
		String name = module.name
		String definition = module.source.contents
		DefinitionLanguage defLang = module.source.definitionLanguage
		def bundleDir = new File(rootDir, "bundles/" + name)
		return ModuleBundleFactory.createDirectory(bundleDir, new ModuleBundleParameters(
				name,
				definition,
				DefinitionLanguage.Spaghetti,
				"1.0",
				ModuleFormat.UMD,
				null,
				javaScript,
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

	abstract protected boolean isTypeScriptDefinitionSupported()
}
