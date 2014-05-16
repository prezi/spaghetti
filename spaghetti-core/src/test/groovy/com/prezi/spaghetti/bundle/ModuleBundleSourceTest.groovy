package com.prezi.spaghetti.bundle

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Created by lptr on 16/05/14.
 */
class ModuleBundleSourceTest extends Specification {
	@Rule
 	public TemporaryFolder tempDir = new TemporaryFolder()

	def "directory has file"() {
		File zip = makeDir()
		def source = new ModuleBundleSource.Directory(zip)
		source.init()

		expect:
		source.hasFile("lajos")
		!source.hasFile("tibor")
	}

	def "directory read through"() {
		File dir = makeDir()
		def source = new ModuleBundleSource.Directory(dir)
		source.init()
		def handler = Mock(ModuleBundleSource.ModuleBundleFileHandler)

		when:
		source.processFiles(handler)

		then:
		1 * handler.handleFile("lajos", { get(it) == "Hello" })
		0 * _
	}

	def "directory read one"() {
		def dir = makeDir()
		def source = new ModuleBundleSource.Directory(dir)
		source.init()
		def handler = Mock(ModuleBundleSource.ModuleBundleFileHandler)

		when:
		source.processFile("lajos", handler)

		then:
		1 * handler.handleFile("lajos", { get(it) == "Hello" })
		0 * _
	}

	def "zip has file"() {
		File zip = makeZip()
		def source = new ModuleBundleSource.Zip(zip)
		source.init()

		expect:
		source.hasFile("lajos")
		!source.hasFile("tibor")
	}

	def "zip read through"() {
		File zip = makeZip()
		def source = new ModuleBundleSource.Zip(zip)
		source.init()
		def handler = Mock(ModuleBundleSource.ModuleBundleFileHandler)

		when:
		source.processFiles(handler)

		then:
		1 * handler.handleFile("lajos", { get(it) == "Hello" })
		0 * _
	}

	def "zip read one"() {
		def zip = makeZip()
		def source = new ModuleBundleSource.Zip(zip)
		source.init()
		def handler = Mock(ModuleBundleSource.ModuleBundleFileHandler)

		when:
		source.processFile("lajos", handler)

		then:
		1 * handler.handleFile("lajos", { get(it) == "Hello" })
		0 * _
	}

	private File makeDir() {
		def dir = tempDir.newFolder()
		new File(dir, "lajos") << "Hello"
		return dir
	}

	private File makeZip() {
		def zip = tempDir.newFile()
		def zipStream = new ZipOutputStream(new FileOutputStream(zip))
		zipStream.putNextEntry(new ZipEntry("lajos"))
		zipStream << "Hello"
		zipStream.close()
		return zip
	}

	private static String get(Callable<InputStream> input) {
		return input().text
	}
}
