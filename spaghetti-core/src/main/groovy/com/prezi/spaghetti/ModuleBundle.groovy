package com.prezi.spaghetti

import groovy.io.FileType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.jar.Attributes
import java.util.jar.Manifest
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

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

	final File zip
	final String name
	final String definition
	final String bundledJavaScript
	final String version
	final String sourceBaseUrl
	final String sourceMap
	final Set<String> resourcePaths

	private ModuleBundle(File zip, String name, String definition, String version, String sourceBaseUrl, String bundledJavaScript, String sourceMap, Set<String> resourcePaths) {
		this.zip = zip
		this.name = name
		this.version = version
		this.sourceBaseUrl = sourceBaseUrl
		this.definition = definition
		this.bundledJavaScript = bundledJavaScript
		this.sourceMap = sourceMap
		this.resourcePaths = resourcePaths
	}

	public static ModuleBundle create(File outputFile, String name, String definition, String version, String sourceBaseUrl, String bundledJavaScript, String sourceMap, Set<File> resourceDirs) {
		checkNotNull(name, "name", [])
		checkNotNull(version, "version", [])
		checkNotNull(sourceBaseUrl, "sourceBaseUrl", [])
		checkNotNull(definition, "definition", [])
		checkNotNull(bundledJavaScript, "bundledJavaScript", [])

		Set<String> resourcePaths = []
		outputFile.delete()
		outputFile.parentFile.mkdirs()
		outputFile.withOutputStream { fos ->
			def zipStream = new ZipOutputStream(fos)
			//noinspection GroovyMissingReturnStatement
			zipStream.withStream {
				// Store manifest
				zipStream.putNextEntry(new ZipEntry(MANIFEST_MF_PATH))
				Manifest manifest = new Manifest()
				manifest.mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0")
				manifest.mainAttributes.put(MANIFEST_ATTR_SPAGHETTI_VERSION, Version.SPAGHETTI_VERSION)
				manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_NAME, name)
				manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_VERSION, version ?: "")
				manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_SOURCE, sourceBaseUrl ?: "")
				manifest.write(zipStream)

				// Store definition
				zipStream.putNextEntry(new ZipEntry(DEFINITION_PATH))
				zipStream << definition

				// Store module itself
				zipStream.putNextEntry(new ZipEntry(COMPILED_JAVASCRIPT_PATH))
				zipStream << bundledJavaScript

				// Store sourcemap
				if (sourceMap != null) {
					zipStream.putNextEntry(new ZipEntry(SOURCE_MAP_PATH));
					zipStream << sourceMap;
				}

				// Store resources
				resourceDirs.each { resourceDir ->
					resourceDir.eachFileRecurse(FileType.FILES) { File resourceFile ->
						def resourcePath = RESOURCES_PREFIX + resourceDir.toURI().relativize(resourceFile.toURI()).toString()
						log.warn("Adding resource {}", resourcePath)
						zipStream.putNextEntry(new ZipEntry(resourcePath))
						zipStream << resourceFile.newInputStream()
						resourcePaths.add resourcePath
					}
				}
				null
			}
		}
		return new ModuleBundle(outputFile, name, definition, version, sourceBaseUrl, bundledJavaScript, sourceMap, resourcePaths.asImmutable())
	}

	public static ModuleBundle load(File inputFile) {
		if (!inputFile.exists()) {
			throw new IllegalArgumentException("Module file not found: ${inputFile}")
		}
		ZipFile zipFile
		try {
			zipFile = new ZipFile(inputFile)
		} catch (Exception ex) {
			throw new IllegalArgumentException("Could not open module ZIP file: ${inputFile}", ex)
		}

		String definition = null
		String compiledJavaScript = null
		Manifest manifest = null
		String sourceMap = null
		Set<String> resourcePaths = []

		zipFile.entries().each { ZipEntry entry ->
			Closure<String> contents = { zipFile.getInputStream(entry).text }
			switch (entry.name) {
				case DEFINITION_PATH:
					definition = contents()
					break
				case COMPILED_JAVASCRIPT_PATH:
					compiledJavaScript = contents()
					break
				case MANIFEST_MF_PATH:
					manifest = new Manifest(zipFile.getInputStream(entry))
					break
				case SOURCE_MAP_PATH:
					sourceMap = contents()
					break
				default:
					if (entry.name.startsWith(RESOURCES_PREFIX)) {
						resourcePaths.add(entry.name.substring(RESOURCES_PREFIX.length()))
					}
					break
			}
		}
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
		String source = manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_SOURCE) ?: "unknown-source"
		return new ModuleBundle(inputFile, name, definition, version, source, compiledJavaScript, sourceMap, resourcePaths.asImmutable())
	}

	private static boolean isSpaghettiVersionSupported(String spaghettiVersion) {
		return spaghettiVersion?.startsWith("1.")
	}

	@Override
	int compareTo(ModuleBundle o) {
		return name.compareTo(o.name)
	}
}
