package com.prezi.gradle.spaghetti

import org.gradle.api.java.archives.Manifest
import org.gradle.api.java.archives.internal.DefaultManifest

import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

import static com.google.common.base.Preconditions.checkNotNull
/**
 * Created by lptr on 16/11/13.
 */
class ModuleBundle {
	static final def MANIFEST_ATTR_MODULE_VERSION = "Module-Version"
	public static final String DEFINITION_PATH = "module.def"
	public static final String COMPILED_JAVASCRIPT_PATH = "module.js"
	public static final String MANIFEST_MF_PATH = "META-INF/MANIFEST.MF"

	final String definition
	final String compiledJavaScript

	public ModuleBundle(String definition, String compiledJavaScript) {
		this.definition = checkNotNull(definition)
		this.compiledJavaScript = checkNotNull(compiledJavaScript)
	}

	public void save(File outputFile) {
		outputFile.delete()
		outputFile.withOutputStream { fos ->
			def zipStream = new ZipOutputStream(fos)
			zipStream.withStream {
				// Store manifest
				zipStream.putNextEntry(new ZipEntry(MANIFEST_MF_PATH))
				Manifest manifest = new DefaultManifest(null)
				manifest.attributes.put(MANIFEST_ATTR_MODULE_VERSION, "1.0")
				manifest.writeTo(new OutputStreamWriter(zipStream, "utf-8"))

				// Store definition
				zipStream.putNextEntry(new ZipEntry(DEFINITION_PATH))
				zipStream << definition

				// Store module itself
				zipStream.putNextEntry(new ZipEntry(COMPILED_JAVASCRIPT_PATH))
				zipStream << "define(function() { var __module;\n"
				zipStream << compiledJavaScript
				zipStream << "return __module;});"
			}
		}
	}

	public static ModuleBundle load(File inputFile) {
		def zipFile = new ZipFile(inputFile)

		String definition = null
		String compiledJavaScript = null
		java.util.jar.Manifest manifest = null

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
					manifest = new java.util.jar.Manifest(zipFile.getInputStream(entry))
					break
			}
		}
		if (manifest == null) {
			throw new IllegalArgumentException("Not a module, missing manifest: " + inputFile)
		}
		def moduleVersion = manifest.mainAttributes.getValue(MANIFEST_ATTR_MODULE_VERSION)
		if (moduleVersion == null) {
			throw new IllegalArgumentException("Not a module, module version missing from manifest: " + inputFile)
		}
		if (moduleVersion != "1.0") {
			throw new IllegalArgumentException("Not a module, module version mismatch (should be \"1.0\", but was \"" + moduleVersion + "\"): " + inputFile)
		}
		if (definition == null) {
			throw new IllegalArgumentException("Not a module, missing definition: " + inputFile)
		}
		if (compiledJavaScript == null) {
			throw new IllegalArgumentException("Not a module, missing compiled JavaScript: " + inputFile)
		}
		return new ModuleBundle(definition, compiledJavaScript)
	}
}
