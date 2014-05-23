package com.prezi.spaghetti.structure

import spock.lang.Specification

import java.util.concurrent.Callable
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Created by lptr on 16/05/14.
 */
class StructuredReaderTest extends Specification {
	private static final File tempDir = {
		def dir = File.createTempFile("bundle-source", ".test")
		dir.delete()
		dir.mkdirs()
		return dir
	}()

	def "has file"() {
		expect:
		source.hasFile("lajos")
		source.hasFile("bela/bela")
		!source.hasFile("tibor")

		where:
		source    | _
		makeZip() | _
		makeDir() | _
	}

	def "read through"() {
		def handler = Mock(StructuredReader.FileHandler)

		when:
		source.processFiles(handler)

		then:
		1 * handler.handleFile("lajos", { get(it) == "Hello" })
		1 * handler.handleFile("bela/bela", { get(it) == "Hi" })
		0 * _

		where:
		source    | _
		makeZip() | _
		makeDir() | _
	}

	def "read one"() {
		def handler = Mock(StructuredReader.FileHandler)

		when:
		source.processFile("lajos", handler)

		then:
		1 * handler.handleFile("lajos", { get(it) == "Hello" })
		0 * _

		where:
		source    | _
		makeZip() | _
		makeDir() | _
	}

	private static StructuredReader makeDir() {
		def dir = File.createTempFile("bundle", ".dir", tempDir)
		dir.delete()
		dir.mkdirs()
		new File(dir, "lajos") << "Hello"
		new File(dir, "bela").mkdirs()
		new File(dir, "bela/bela") << "Hi"
		def source = new StructuredReader.Directory(dir)
		source.init()
		return source
	}

	private static StructuredReader makeZip() {
		def zip = File.createTempFile("bundle", ".zip", tempDir)
		def zipStream = new ZipOutputStream(new FileOutputStream(zip))
		zipStream.putNextEntry(new ZipEntry("lajos"))
		zipStream << "Hello"
		zipStream.putNextEntry(new ZipEntry("bela/bela"))
		zipStream << "Hi"
		zipStream.close()
		def source = new StructuredReader.Zip(zip)
		source.init()
		return source
	}

	private static String get(Callable<InputStream> input) {
		return input().text
	}
}
