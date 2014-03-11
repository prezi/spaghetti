package com.prezi.spaghetti

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
	static final def MANIFEST_ATTR_SPAGHETTI_VERSION = new Attributes.Name("Spaghetti-Version")
	static final def MANIFEST_ATTR_MODULE_NAME = new Attributes.Name("Module-Name")
	static final def MANIFEST_ATTR_MODULE_VERSION = new Attributes.Name("Module-Version")
	static final def MANIFEST_ATTR_MODULE_SOURCE = new Attributes.Name("Module-Source")
	public static final String DEFINITION_PATH = "module.def"
	public static final String SOURCE_MAP_PATH = "module.map"
	public static final String COMPILED_JAVASCRIPT_PATH = "module.js"
	public static final String MANIFEST_MF_PATH = "META-INF/MANIFEST.MF"

	final String name
	final String definition
	final String bundledJavaScript
	final String version
	final String source
	final String sourceMap

	public ModuleBundle(String name, String definition, String version, String source, String bundledJavaScript, String sourceMap) {
		this.name = checkNotNull(name)
		this.version = version ?: ""
		this.source = source ?: ""
		this.definition = checkNotNull(definition)
		this.bundledJavaScript = checkNotNull(bundledJavaScript)
		this.sourceMap = sourceMap;
	}

	public void save(File outputFile) {
		outputFile.delete()
		outputFile.parentFile.mkdirs()
		outputFile.withOutputStream { fos ->
			def zipStream = new ZipOutputStream(fos)
			zipStream.withStream {
				// Store manifest
				zipStream.putNextEntry(new ZipEntry(MANIFEST_MF_PATH))
				Manifest manifest = new Manifest()
				manifest.mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0")
				manifest.mainAttributes.put(MANIFEST_ATTR_SPAGHETTI_VERSION, Version.SPAGHETTI_VERSION)
				manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_NAME, name)
				manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_VERSION, version)
				manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_SOURCE, source)
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
			}
		}
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
		return new ModuleBundle(name, definition, version, source, compiledJavaScript, sourceMap)
	}

	private static boolean isSpaghettiVersionSupported(String spaghettiVersion) {
		return spaghettiVersion?.startsWith("1.")
	}

	@Override
	int compareTo(ModuleBundle o) {
		return name.compareTo(o.name)
	}
}
