package com.prezi.gradle.spaghetti

import java.util.jar.Attributes
import java.util.jar.Manifest
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

import static com.google.common.base.Preconditions.checkNotNull
/**
 * Created by lptr on 16/11/13.
 */
class ModuleBundle {
	static final def MANIFEST_ATTR_SPAGHETTI_VERSION = new Attributes.Name("Spaghetti-Version")
	static final def MANIFEST_ATTR_MODULE_NAME = new Attributes.Name("Module-Name")
	public static final String DEFINITION_PATH = "module.def"
	public static final String COMPILED_JAVASCRIPT_PATH = "module.js"
	public static final String MANIFEST_MF_PATH = "META-INF/MANIFEST.MF"

	final FQName name
	final String definition
	final String bundledJavaScript

	public ModuleBundle(FQName name, String definition, String bundledJavaScript) {
		this.name = checkNotNull(name)
		this.definition = checkNotNull(definition)
		this.bundledJavaScript = checkNotNull(bundledJavaScript)
	}

	public void save(File outputFile) {
		outputFile.delete()
		outputFile.withOutputStream { fos ->
			def zipStream = new ZipOutputStream(fos)
			zipStream.withStream {
				// Store manifest
				zipStream.putNextEntry(new ZipEntry(MANIFEST_MF_PATH))
				Manifest manifest = new Manifest()
				manifest.mainAttributes.put(Attributes.Name.MANIFEST_VERSION, "1.0")
				manifest.mainAttributes.put(MANIFEST_ATTR_SPAGHETTI_VERSION, "1.0")
				manifest.mainAttributes.put(MANIFEST_ATTR_MODULE_NAME, name.fullyQualifiedName)
				manifest.write(zipStream)

				// Store definition
				zipStream.putNextEntry(new ZipEntry(DEFINITION_PATH))
				zipStream << definition

				// Store module itself
				zipStream.putNextEntry(new ZipEntry(COMPILED_JAVASCRIPT_PATH))
				zipStream << bundledJavaScript
			}
		}
	}

	public static ModuleBundle load(File inputFile) {
		def zipFile = new ZipFile(inputFile)

		String definition = null
		String compiledJavaScript = null
		Manifest manifest = null

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
			}
		}
		if (manifest == null) {
			throw new IllegalArgumentException("Not a module, missing manifest: " + inputFile)
		}
		def spaghettiVersion = manifest.mainAttributes.getValue(MANIFEST_ATTR_SPAGHETTI_VERSION)
		if (spaghettiVersion == null) {
			throw new IllegalArgumentException("Not a module, module version missing from manifest: " + inputFile)
		}
		if (spaghettiVersion != "1.0") {
			throw new IllegalArgumentException("Not a module, module version mismatch (should be \"1.0\", but was \"" + spaghettiVersion + "\"): " + inputFile)
		}
		FQName name = FQName.fromString(manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_NAME))
		if (definition == null) {
			throw new IllegalArgumentException("Not a module, missing definition: " + inputFile)
		}
		if (compiledJavaScript == null) {
			throw new IllegalArgumentException("Not a module, missing compiled JavaScript: " + inputFile)
		}
		return new ModuleBundle(name, definition, compiledJavaScript)
	}
}
