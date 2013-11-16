package com.prezi.gradle.spaghetti

import org.gradle.api.java.archives.Manifest
import org.gradle.api.java.archives.internal.DefaultManifest

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

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
		this.definition = definition
		this.compiledJavaScript = compiledJavaScript
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

	public static ModuleBundle load(File inputFile, File outputDirectory) {
		def ant = new AntBuilder()
		ant.unzip(
				src: inputFile,
				dest: outputDirectory,
				overwrite: true
		)

		def definition = new File(outputDirectory, DEFINITION_PATH).text
		def compiledJavaScript = new File(outputDirectory, COMPILED_JAVASCRIPT_PATH).text

		return new ModuleBundle(definition, compiledJavaScript)
	}
}
