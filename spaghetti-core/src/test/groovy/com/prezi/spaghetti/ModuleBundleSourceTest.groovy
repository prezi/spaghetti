package com.prezi.spaghetti

import com.prezi.spaghetti.bundle.ModuleBundleSource
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

	def "directory"() {
		def dir = tempDir.newFolder()
		new File(dir, "lajos") << "Hello"
		def source = new ModuleBundleSource.Directory(dir)
		def handler = Mock(ModuleBundleSource.ModuleBundleFileHandler)

		when:
		source.processFiles(handler)

		then:
		1 * handler.handleFile("lajos", { get(it) == "Hello" })
		0 * _
	}

	def "zip"() {
		def zip = tempDir.newFile()
		def zipStream = new ZipOutputStream(new FileOutputStream(zip))
		zipStream.putNextEntry(new ZipEntry("lajos"))
		zipStream << "Hello"
		zipStream.close()
		def source = new ModuleBundleSource.Zip(zip)
		def handler = Mock(ModuleBundleSource.ModuleBundleFileHandler)

		when:
		source.processFiles(handler)

		then:
		1 * handler.handleFile("lajos", { get(it) == "Hello" })
		0 * _
	}

	private static String get(Callable<InputStream> input) {
		return input().text
	}
}
