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
class DefaultModuleBundle extends AbstractModuleBundle {
	private static final Logger logger = LoggerFactory.getLogger(DefaultModuleBundle)

	private static final def MANIFEST_ATTR_SPAGHETTI_VERSION = new Attributes.Name("Spaghetti-Version")
	private static final def MANIFEST_ATTR_MODULE_NAME = new Attributes.Name("Module-Name")
	private static final def MANIFEST_ATTR_MODULE_VERSION = new Attributes.Name("Module-Version")
	private static final def MANIFEST_ATTR_MODULE_TYPE = new Attributes.Name("Module-Type")
	private static final def MANIFEST_ATTR_MODULE_SOURCE = new Attributes.Name("Module-Source")
	private static final def MANIFEST_ATTR_MODULE_DEPENDENCIES = new Attributes.Name("Module-Dependencies")

	protected final BundleSource source

	protected DefaultModuleBundle(BundleSource source, String name, ModuleType type, String version, String sourceBaseUrl, Set<String> dependentModules, Set<String> resourcePaths) {
		super(name, type, version, sourceBaseUrl, dependentModules, resourcePaths)
		this.source = source
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
			builder.appendFile(ModuleBundle.MANIFEST_MF_PATH, { out -> manifest.write(out) })

			// Store definition
			builder.appendFile ModuleBundle.DEFINITION_PATH, { out -> out << params.definition }

			// Store module itself
			builder.appendFile ModuleBundle.JAVASCRIPT_PATH, { out -> out << params.javaScript }

			// Store sourcemap
			if (params.sourceMap != null) {
				builder.appendFile ModuleBundle.SOURCE_MAP_PATH, { out -> out << params.sourceMap }
			}

			// Store resources
			def resourceDir = params.resourcesDirectory
			if (resourceDir?.exists()) {
				resourceDir.eachFileRecurse(FileType.FILES) { File resourceFile ->
					def resourcePath = ModuleBundle.RESOURCES_PREFIX + resourceDir.toURI().relativize(resourceFile.toURI()).toString()
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
		if (!source.hasFile(ModuleBundle.MANIFEST_MF_PATH)) {
			throw new IllegalArgumentException("Not a module, missing manifest: " + source)
		}
		if (!source.hasFile(ModuleBundle.DEFINITION_PATH)) {
			throw new IllegalArgumentException("Not a module, missing definition: ${source}")
		}
		if (!source.hasFile(ModuleBundle.JAVASCRIPT_PATH)) {
			throw new IllegalArgumentException("Not a module, missing JavaScript: ${source}")
		}

		Manifest manifest = null
		Set<String> resourcePaths = []
		source.processFiles(new BundleSource.ModuleBundleFileHandler() {
			@Override
			void handleFile(String path, Callable<? extends InputStream> contents) {
				switch (path) {
					case ModuleBundle.MANIFEST_MF_PATH:
						manifest = new Manifest(contents())
						break
					default:
						if (path.startsWith(ModuleBundle.RESOURCES_PREFIX)) {
							resourcePaths.add(path.substring(ModuleBundle.RESOURCES_PREFIX.length()))
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
					case ModuleBundle.MANIFEST_MF_PATH:
						break
					case ModuleBundle.DEFINITION_PATH:
						if (elements.contains(ModuleBundleElement.definition)) {
							output.appendFile "${name}.def", write(contents)
						}
						break
					case ModuleBundle.JAVASCRIPT_PATH:
						if (elements.contains(ModuleBundleElement.javascript)) {
							output.appendFile "${name}.js", write(contents)
						}
						break
					case ModuleBundle.SOURCE_MAP_PATH:
						if (elements.contains(ModuleBundleElement.sourcemap)) {
							output.appendFile "${name}.js.map", write(contents)
						}
						break
					default:
						if (elements.contains(ModuleBundleElement.resources) && path.startsWith(ModuleBundle.RESOURCES_PREFIX)) {
							def resourcePath = path.substring(ModuleBundle.RESOURCES_PREFIX.length())
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
}
