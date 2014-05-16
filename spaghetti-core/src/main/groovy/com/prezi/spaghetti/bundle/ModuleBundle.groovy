package com.prezi.spaghetti.bundle

import com.prezi.spaghetti.Version
import com.prezi.spaghetti.bundle.BundleBuilder.BundleAppender
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
	private static final Logger logger = LoggerFactory.getLogger(ModuleBundle)

	private static final def MANIFEST_ATTR_SPAGHETTI_VERSION = new Attributes.Name("Spaghetti-Version")
	private static final def MANIFEST_ATTR_MODULE_NAME = new Attributes.Name("Module-Name")
	private static final def MANIFEST_ATTR_MODULE_VERSION = new Attributes.Name("Module-Version")
	private static final def MANIFEST_ATTR_MODULE_SOURCE = new Attributes.Name("Module-Source")
	private static final def MANIFEST_ATTR_MODULE_DEPENDENCIES = new Attributes.Name("Module-Dependencies")

	protected static final def DEFINITION_PATH = "module.def"
	protected static final def SOURCE_MAP_PATH = "module.map"
	protected static final def JAVASCRIPT_PATH = "module.js"
	protected static final def MANIFEST_MF_PATH = "META-INF/MANIFEST.MF"
	protected static final def RESOURCES_PREFIX = "resources/"

	protected final BundleSource source
	final String name
	final String version
	final String sourceBaseUrl
	final Set<String> dependentModules
	final Set<String> resourcePaths

	protected ModuleBundle(BundleSource source, String name, String version, String sourceBaseUrl, Set<String> dependentModules, Set<String> resourcePaths) {
		this.source = source
		this.name = name
		this.version = version
		this.sourceBaseUrl = sourceBaseUrl
		this.dependentModules = dependentModules
		this.resourcePaths = resourcePaths
	}

	public String getDefinition() {
		return getString(DEFINITION_PATH)
	}

	public String getJavaScript() {
		return getString(JAVASCRIPT_PATH)
	}

	public String getSourceMap() {
		return getString(SOURCE_MAP_PATH)
	}

	public static ModuleBundle createZip(File outputFile, ModuleBundleParameters params) {
		return create(new BundleBuilder.Zip(outputFile), params)
	}

	public static ModuleBundle createDirectory(File outputDirectory, ModuleBundleParameters params) {
		return create(new BundleBuilder.Directory(outputDirectory), params)
	}

	protected static ModuleBundle create(BundleBuilder builder, ModuleBundleParameters params) {
		checkNotNull(params.name, "name", [])
		checkNotNull(params.version, "version", [])
		checkNotNull(params.definition, "definition", [])
		checkNotNull(params.javaScript, "javaScript", [])

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
			manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_DEPENDENCIES, params.dependentModules.join(","))
			builder.addEntry MANIFEST_MF_PATH, { out -> manifest.write(out) }

			// Store definition
			builder.addEntry DEFINITION_PATH, { out -> out << params.definition }

			// Store module itself
			builder.addEntry JAVASCRIPT_PATH, { out -> out << params.javaScript }

			// Store sourcemap
			if (params.sourceMap != null) {
				builder.addEntry SOURCE_MAP_PATH, { out -> out << params.sourceMap }
			}

			// Store resources
			def resourceDir = params.resourcesDirectory
			if (resourceDir?.exists()) {
				resourceDir.eachFileRecurse(FileType.FILES) { File resourceFile ->
					def resourcePath = RESOURCES_PREFIX + resourceDir.toURI().relativize(resourceFile.toURI()).toString()
					logger.debug("Adding resource {}", resourcePath)
					builder.addEntry resourcePath, { out -> resourceFile.withInputStream { out << it } }
					resourcePaths.add resourcePath
				}
			}

			def source = builder.create()
			return new ModuleBundle(source, params.name, params.version, params.sourceBaseUrl, params.dependentModules, resourcePaths.asImmutable())
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
			logger.debug "{} is a file, trying to load as ZIP", inputFile
			source = new BundleSource.Zip(inputFile)
		} else if (inputFile.directory) {
			logger.debug "{} is a directory, trying to load as exploded", inputFile
			source = new BundleSource.Directory(inputFile)
		} else {
			throw new RuntimeException("Unknown module format: ${inputFile}")
		}

		source.init()
		try {
			return loadInternal(source)
		} finally {
			source.close()
		}
	}

	protected static ModuleBundle loadInternal(BundleSource source) {
		if (!source.hasFile(MANIFEST_MF_PATH)) {
			throw new IllegalArgumentException("Not a module, missing manifest: " + source)
		}
		if (!source.hasFile(DEFINITION_PATH)) {
			throw new IllegalArgumentException("Not a module, missing definition: ${source}")
		}
		if (!source.hasFile(JAVASCRIPT_PATH)) {
			throw new IllegalArgumentException("Not a module, missing JavaScript: ${source}")
		}

		Manifest manifest = null
		Set<String> resourcePaths = []
		source.processFiles(new BundleSource.ModuleBundleFileHandler() {
			@Override
			void handleFile(String path, Callable<? extends InputStream> contents) {
				switch (path) {
					case MANIFEST_MF_PATH:
						manifest = new Manifest(contents())
						break
					default:
						if (path.startsWith(RESOURCES_PREFIX)) {
							resourcePaths.add(path.substring(RESOURCES_PREFIX.length()))
						}
						break
				}
			}
		})

		def spaghettiVersion = manifest.mainAttributes.getValue(MANIFEST_ATTR_SPAGHETTI_VERSION)
		if (spaghettiVersion == null) {
			throw new IllegalArgumentException("Not a module, module version missing from manifest: ${source}")
		}
		if (!isSpaghettiVersionSupported(spaghettiVersion)) {
			throw new IllegalArgumentException("Spaghetti version mismatch (should be 1.x), but was \"${spaghettiVersion}\"): ${source}")
		}
		String name = manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_NAME)
		String version = manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_VERSION) ?: "unknown-version"
		String sourceUrl = manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_SOURCE) ?: "unknown-source"
		Set<String> dependentModules = manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_DEPENDENCIES)?.tokenize(",") ?: []
		return new ModuleBundle(source, name, version, sourceUrl, dependentModules, resourcePaths.asImmutable())
	}

	public void extract(File outputDirectory, ModuleBundleElement... elements) {
		extract(outputDirectory, elements ? EnumSet.of(*elements) : EnumSet.allOf(ModuleBundleElement))
	}

	public void extract(File outputDirectory, EnumSet<ModuleBundleElement> elements = EnumSet.allOf(ModuleBundleElement)) {
		source.init()
		try {
			def output = new BundleBuilder.Directory(outputDirectory)
			output.init()
			try {
				extract(name, source, output, elements)
			} finally {
				output.close()
			}
		} finally {
			source.close()
		}
	}

	private getString(String path) {
		source.init()
		try {
			if (!source.hasFile(path)) {
				return null
			}
			String text = null
			source.processFile(path, new BundleSource.ModuleBundleFileHandler() {
				@Override
				void handleFile(String _, Callable<? extends InputStream> contents) {
					text = contents().getText("utf-8")
				}
			})
			return text
		} finally {
			source.close()
		}
	}

	protected static void extract(String name, BundleSource source, BundleAppender output, EnumSet<ModuleBundleElement> elements = EnumSet.allOf(ModuleBundleElement)) {
		source.processFiles(new BundleSource.ModuleBundleFileHandler() {
			@Override
			void handleFile(String path, Callable<? extends InputStream> contents) {
				switch (path) {
					case MANIFEST_MF_PATH:
						break
					case DEFINITION_PATH:
						if (elements.contains(ModuleBundleElement.definition)) {
							output.appendFile "${name}.def", write(contents)
						}
						break
					case JAVASCRIPT_PATH:
						if (elements.contains(ModuleBundleElement.javascript)) {
							output.appendFile "${name}.js", write(contents)
						}
						break
					case SOURCE_MAP_PATH:
						if (elements.contains(ModuleBundleElement.sourcemap)) {
							output.appendFile "${name}.js.map", write(contents)
						}
						break
					default:
						if (elements.contains(ModuleBundleElement.resources) && path.startsWith(RESOURCES_PREFIX)) {
							def resourcePath = path.substring(RESOURCES_PREFIX.length())
							// Skip the resources directory itself
							if (resourcePath) {
								output.appendFile resourcePath, write(contents)
							}
						}
						break
				}
			}

			Closure write(Callable<? extends InputStream> contents) {
				{ out -> out << contents() }
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
