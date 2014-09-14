package com.prezi.spaghetti.structure

import com.prezi.spaghetti.structure.internal.StructuredDirectoryProcessor
import com.prezi.spaghetti.structure.internal.StructuredDirectoryWriter
import com.prezi.spaghetti.structure.internal.StructuredZipProcessor
import com.prezi.spaghetti.structure.internal.StructuredZipWriter
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.util.zip.ZipFile

class StructuredWriterTest extends Specification {
	@Rule
 	public TemporaryFolder tempDir = new TemporaryFolder()

	def "directory created even if there was a file in its place"() {
		def dir = tempDir.newFolder()
		dir.createNewFile()
		def builder = new StructuredDirectoryWriter(dir)
		builder.init()

		expect:
		dir.directory
	}

	def "directory"() {
		def dir = tempDir.newFolder()
		def builder = new StructuredDirectoryWriter(dir)
		builder.init()
		builder.appendFile("lajos", "Hello")
		def source = builder.create()

		expect:
		def lajos = new File(dir, "lajos")
		lajos.file
		lajos.text == "Hello"
		source instanceof StructuredDirectoryProcessor
		((StructuredDirectoryProcessor) source).sourceDirectory == dir
	}

	def "zip is created even if there was a directory in its place"() {
		def zip = tempDir.newFile()
		zip.mkdirs()
		def builder = new StructuredZipWriter(zip)
		builder.init()

		expect:
		zip.file
	}

	def "zip"() {
		def zip = tempDir.newFile()
		def builder = new StructuredZipWriter(zip)
		builder.init()
		builder.appendFile("lajos", "Hello")
		def source = builder.create()

		def zipFile = new ZipFile(zip)

		expect:
		zipFile.getInputStream(zipFile.getEntry("lajos")).text == "Hello"
		source instanceof StructuredZipProcessor
		((StructuredZipProcessor) source).zip == zip
	}
}
