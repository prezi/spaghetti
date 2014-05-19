package com.prezi.spaghetti.bundle

import com.prezi.spaghetti.Version
import com.prezi.spaghetti.bundle.BundleBuilder.BundleAppender
import com.prezi.spaghetti.definition.ModuleType
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
class DefaultModuleBundle implements ModuleBundle {
	private static final Logger logger = LoggerFactory.getLogger(DefaultModuleBundle)

	protected static final def DEFINITION_PATH = "module.def"
	protected static final def SOURCE_MAP_PATH = "module.map"
	protected static final def JAVASCRIPT_PATH = "module.js"
	protected static final def MANIFEST_MF_PATH = "META-INF/MANIFEST.MF"
	protected static final def RESOURCES_PREFIX = "resources/"

	private static final def MANIFEST_ATTR_SPAGHETTI_VERSION = new Attributes.Name("Spaghetti-Version")
	private static final def MANIFEST_ATTR_MODULE_NAME = new Attributes.Name("Module-Name")
	private static final def MANIFEST_ATTR_MODULE_VERSION = new Attributes.Name("Module-Version")
	private static final def MANIFEST_ATTR_MODULE_TYPE = new Attributes.Name("Module-Type")
	private static final def MANIFEST_ATTR_MODULE_SOURCE = new Attributes.Name("Module-Source")
	private static final def MANIFEST_ATTR_MODULE_DEPENDENCIES = new Attributes.Name("Module-Dependencies")

	protected final BundleSource source
	final String name
	final ModuleType type
	final String version
	final String sourceBaseUrl
	final Set<String> dependentModules
	final Set<String> resourcePaths

	protected DefaultModuleBundle(BundleSource source, String name, ModuleType type, String version, String sourceBaseUrl, Set<String> dependentModules, Set<String> resourcePaths) {
		this.source = source
		this.name = name
		this.type = type
		this.version = version
		this.sourceBaseUrl = sourceBaseUrl
		this.dependentModules = dependentModules
		this.resourcePaths = resourcePaths
	}

	@Override
	public String getDefinition() {
		return getString(DEFINITION_PATH)
	}

	@Override
	public String getJavaScript() {
		return getString(JAVASCRIPT_PATH)
	}

	@Override
	public String getSourceMap() {
		return getString(SOURCE_MAP_PATH)
	}

	protected static DefaultModuleBundle create(BundleBuilder builder, ModuleBundleParameters params) {
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
			manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_TYPE, params.type.name().toLowerCase())
			manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_VERSION, params.version ?: "")
			manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_SOURCE, params.sourceBaseUrl ?: "")
			manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_DEPENDENCIES, params.dependentModules.join(","))
			builder.appendFile MANIFEST_MF_PATH, { out -> manifest.write(out) }

			// Store definition
			builder.appendFile DEFINITION_PATH, { out -> out << params.definition }

			// Store module itself
			builder.appendFile JAVASCRIPT_PATH, { out -> out << params.javaScript }

			// Store sourcemap
			if (params.sourceMap != null) {
				builder.appendFile SOURCE_MAP_PATH, { out -> out << params.sourceMap }
			}

			// Store resources
			def resourceDir = params.resourcesDirectory
			if (resourceDir?.exists()) {
				resourceDir.eachFileRecurse(FileType.FILES) { File resourceFile ->
					def resourcePath = RESOURCES_PREFIX + resourceDir.toURI().relativize(resourceFile.toURI()).toString()
					logger.debug("Adding resource {}", resourcePath)
					builder.appendFile resourcePath, { out -> resourceFile.withInputStream { out << it } }
					resourcePaths.add resourcePath
				}
			}

			def source = builder.create()
			return new DefaultModuleBundle(source, params.name, params.type, params.version, params.sourceBaseUrl, params.dependentModules, resourcePaths.asImmutable())
		} finally {
			builder.close()
		}
	}

	protected static DefaultModuleBundle loadInternal(BundleSource source) {
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
		ModuleType type = ModuleType.valueOf(manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_TYPE).toUpperCase())
		String version = manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_VERSION) ?: "unknown-version"
		String sourceUrl = manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_SOURCE) ?: "unknown-source"
		Set<String> dependentModules = manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_DEPENDENCIES)?.tokenize(",") ?: []
		return new DefaultModuleBundle(source, name, type, version, sourceUrl, dependentModules, resourcePaths.asImmutable())
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

	@Override
	void extract(BundleAppender output, EnumSet<ModuleBundleElement> elements) {
		source.init()
		try {
			extract(name, source, output, elements)
		} finally {
			source.close()
		}
	}

	protected static void extract(String name, BundleSource source, BundleAppender output, EnumSet<ModuleBundleElement> elements) {
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
		return spaghettiVersion?.startsWith("2.")
	}

	@Override
	int compareTo(ModuleBundle o) {
		return name.compareTo(o.name)
	}
}
