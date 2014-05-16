package com.prezi.spaghetti.bundle

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.util.zip.ZipFile

/**
 * Created by lptr on 16/05/14.
 */
class BundleBuilderTest extends Specification {
	@Rule
 	public TemporaryFolder tempDir = new TemporaryFolder()

	def "directory created even if there was a file in its place"() {
		def dir = tempDir.newFolder()
		dir.createNewFile()
		def builder = new BundleBuilder.Directory(dir)
		builder.init()

		expect:
		dir.directory
	}

	def "directory"() {
		def dir = tempDir.newFolder()
		def builder = new BundleBuilder.Directory(dir)
		builder.init()
		builder.appendFile("lajos", { out -> out << "Hello" })
		def source = builder.create()

		expect:
		def lajos = new File(dir, "lajos")
		lajos.file
		lajos.text == "Hello"
		source instanceof BundleSource.Directory
		((BundleSource.Directory) source).sourceDirectory == dir
	}

	def "zip is created even if there was a directory in its place"() {
		def zip = tempDir.newFile()
		zip.mkdirs()
		def builder = new BundleBuilder.Zip(zip)
		builder.init()

		expect:
		zip.file
	}

	def "zip"() {
		def zip = tempDir.newFile()
		def builder = new BundleBuilder.Zip(zip)
		builder.init()
		builder.appendFile("lajos", { out -> out << "Hello" })
		def source = builder.create()

		def zipFile = new ZipFile(zip)

		expect:
		zipFile.getInputStream(zipFile.getEntry("lajos")).text == "Hello"
		source instanceof BundleSource.Zip
		((BundleSource.Zip) source).zip == zip
	}
}