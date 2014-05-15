package com.prezi.spaghetti

import groovy.io.FileType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.Callable
import java.util.jar.Attributes
import java.util.jar.Manifest

import static com.google.common.base.Preconditions.checkNotNull

/**
 * Created by lptr on 16/11/13.
 */
class ModuleBundle implements Comparable<ModuleBundle> {
	private static final Logger log = LoggerFactory.getLogger(ModuleBundle)

	private static final def MANIFEST_ATTR_SPAGHETTI_VERSION = new Attributes.Name("Spaghetti-Version")
	private static final def MANIFEST_ATTR_MODULE_NAME = new Attributes.Name("Module-Name")
	private static final def MANIFEST_ATTR_MODULE_VERSION = new Attributes.Name("Module-Version")
	private static final def MANIFEST_ATTR_MODULE_SOURCE = new Attributes.Name("Module-Source")
	private static final def DEFINITION_PATH = "module.def"
	private static final def SOURCE_MAP_PATH = "module.map"
	private static final def COMPILED_JAVASCRIPT_PATH = "module.js"
	private static final def MANIFEST_MF_PATH = "META-INF/MANIFEST.MF"
	private static final def RESOURCES_PREFIX = "resources/"

	private final ModuleBundleSource source
	final String name
	final String definition
	final String bundledJavaScript
	final String version
	final String sourceBaseUrl
	final String sourceMap
	final Set<String> resourcePaths

	private ModuleBundle(ModuleBundleSource source, String name, String definition, String version, String sourceBaseUrl, String bundledJavaScript, String sourceMap, Set<String> resourcePaths) {
		this.source = source
		this.name = name
		this.version = version
		this.sourceBaseUrl = sourceBaseUrl
		this.definition = definition
		this.bundledJavaScript = bundledJavaScript
		this.sourceMap = sourceMap
		this.resourcePaths = resourcePaths
	}

	public static ModuleBundle createZip(File outputFile, ModubleBundleParameters params) {
		return create(new ModuleBundleBuilder.Zip(outputFile), params)
	}

	public static ModuleBundle createDirectory(File outputDirectory, ModubleBundleParameters params) {
		return create(new ModuleBundleBuilder.Directory(outputDirectory), params)
	}

	@groovy.transform.PackageScope static ModuleBundle create(ModuleBundleBuilder builder, ModubleBundleParameters params) {
		checkNotNull(params.name, "name", [])
		checkNotNull(params.version, "version", [])
		checkNotNull(params.definition, "definition", [])
		checkNotNull(params.bundledJavaScript, "bundledJavaScript", [])

		builder.init()
		try {
			Set<String> resourcePaths = []

			// Store manifest
			Manifest manifest = new Manifest()
			manifest.mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0")
			manifest.mainAttributes.put(MANIFEST_ATTR_SPAGHETTI_VERSION, Version.SPAGHETTI_VERSION)
			manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_NAME, params.name)
			manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_VERSION, params.version ?: "")
			manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_SOURCE, params.sourceBaseUrl ?: "")
			builder.addEntry MANIFEST_MF_PATH, { out -> manifest.write(out) }

			// Store definition
			builder.addEntry DEFINITION_PATH, { out -> out << params.definition }

			// Store module itself
			builder.addEntry COMPILED_JAVASCRIPT_PATH, { out -> out << params.bundledJavaScript }

			// Store sourcemap
			if (params.sourceMap != null) {
				builder.addEntry SOURCE_MAP_PATH, { out -> out << params.sourceMap }
			}

			// Store resources
			params.resourceDirs.each { resourceDir ->
				resourceDir.eachFileRecurse(FileType.FILES) { File resourceFile ->
					def resourcePath = RESOURCES_PREFIX + resourceDir.toURI().relativize(resourceFile.toURI()).toString()
					log.warn("Adding resource {}", resourcePath)
					builder.addEntry resourcePath, { out -> resourceFile.withInputStream { out << it } }
					resourcePaths.add resourcePath
				}
			}

			def source = builder.create()
			return new ModuleBundle(source, params.name, params.definition, params.version, params.sourceBaseUrl, params.bundledJavaScript, params.sourceMap, resourcePaths.asImmutable())
		} finally {
			builder.close()
		}
	}

	public static ModuleBundle load(File inputFile) {
		if (!inputFile.exists()) {
			throw new IllegalArgumentException("Module not found: ${inputFile}")
		}
		def source
		if (inputFile.file) {
			source = new ModuleBundleSource.Zip(inputFile)
		} else {
			source = new ModuleBundleSource.Directory(inputFile)
		}

		String definition = null
		String compiledJavaScript = null
		Manifest manifest = null
		String sourceMap = null
		Set<String> resourcePaths = []

		source.processFiles(new ModuleBundleSource.ModuleBundleFileHandler() {
			@Override
			void handleFile(String path, Callable<? extends InputStream> contents) {
				switch (path) {
					case DEFINITION_PATH:
						definition = contents()
						break
					case COMPILED_JAVASCRIPT_PATH:
						compiledJavaScript = contents()
						break
					case MANIFEST_MF_PATH:
						manifest = new Manifest(contents())
						break
					case SOURCE_MAP_PATH:
						sourceMap = contents()
						break
					default:
						if (path.startsWith(RESOURCES_PREFIX)) {
							resourcePaths.add(path.substring(RESOURCES_PREFIX.length()))
						}
						break
				}
			}
		})
		if (manifest == null) {
			throw new IllegalArgumentException("Not a module, missing manifest: " + inputFile)
		}
		def spaghettiVersion = manifest.mainAttributes.getValue(MANIFEST_ATTR_SPAGHETTI_VERSION)
		if (spaghettiVersion == null) {
			throw new IllegalArgumentException("Not a module, module version missing from manifest: ${inputFile}")
		}
		if (!isSpaghettiVersionSupported(spaghettiVersion)) {
			throw new IllegalArgumentException("Spaghetti version mismatch (should be 1.x), but was \"${spaghettiVersion}\"): ${inputFile}")
		}
		String name = manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_NAME)
		if (definition == null) {
			throw new IllegalArgumentException("Not a module, missing definition: ${inputFile}")
		}
		if (compiledJavaScript == null) {
			throw new IllegalArgumentException("Not a module, missing compiled JavaScript: ${inputFile}")
		}

		String version = manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_VERSION) ?: "unknown-version"
		String sourceUrl = manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_SOURCE) ?: "unknown-source"
		return new ModuleBundle(source, name, definition, version, sourceUrl, compiledJavaScript, sourceMap, resourcePaths.asImmutable())
	}

	public void extract(File outputDirectory, ModuleBundleElement... elements) {
		extract(outputDirectory, elements ? EnumSet.of(*elements) : EnumSet.allOf(ModuleBundleElement))
	}

	public void extract(File outputDirectory, EnumSet<ModuleBundleElement> elements = EnumSet.allOf(ModuleBundleElement)) {
		outputDirectory.delete() || outputDirectory.deleteDir()
		outputDirectory.mkdirs()
		source.processFiles(new ModuleBundleSource.ModuleBundleFileHandler() {
			@Override
			void handleFile(String path, Callable<? extends InputStream> contents) {
				switch (path) {
					case MANIFEST_MF_PATH:
						break
					case DEFINITION_PATH:
						if (elements.contains(ModuleBundleElement.definition)) {
							new File(outputDirectory, "${name}.def") << contents()
						}
						break
					case COMPILED_JAVASCRIPT_PATH:
						if (elements.contains(ModuleBundleElement.javascript)) {
							new File(outputDirectory, "${name}.js") << contents()
						}
						break
					case SOURCE_MAP_PATH:
						if (elements.contains(ModuleBundleElement.sourcemap)) {
							new File(outputDirectory, "${name}.js.map") << contents()
						}
						break
					default:
						if (elements.contains(ModuleBundleElement.resources) && path.startsWith(RESOURCES_PREFIX)) {
							def resourcePath = path.substring(RESOURCES_PREFIX.length())
							def resourceFile = new File(outputDirectory, resourcePath)
							resourceFile.parentFile.mkdirs()
							resourceFile << contents()
						}
						break
				}
			}
		})
	}

	private static boolean isSpaghettiVersionSupported(String spaghettiVersion) {
		return spaghettiVersion?.startsWith("1.")
	}

	@Override
	int compareTo(ModuleBundle o) {
		return name.compareTo(o.name)
	}
}

@groovy.transform.Immutable
class ModubleBundleParameters {
	String name
	String definition
	String version
	String sourceBaseUrl
	String bundledJavaScript
	String sourceMap
	Set<File> resourceDirs
}
