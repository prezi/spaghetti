package com.prezi.spaghetti.bundle

import groovy.io.FileType

import java.util.concurrent.Callable
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Created by lptr on 15/05/14.
 */
public interface ModuleBundleSource {
	void init()
	void processFiles(ModuleBundleFileHandler handler)
	void close()

	interface ModuleBundleFileHandler {
		void handleFile(String path, Callable<? extends InputStream> contents)
	}

	static class Directory implements ModuleBundleSource {
		final File sourceDirectory

		Directory(File sourceDirectory) {
			this.sourceDirectory = sourceDirectory
		}

		@Override
		void init() {
			if (!sourceDirectory.exists()) {
				throw new IllegalArgumentException("Could not find module bundle directory: ${sourceDirectory}")
			}
		}

		@Override
		void processFiles(ModuleBundleSource.ModuleBundleFileHandler handler) {
			sourceDirectory.eachFile(FileType.FILES) { File file ->
				def path = sourceDirectory.toURI().relativize(file.toURI()).toString()
				handler.handleFile(path, { new FileInputStream(file) })
			}
		}

		@Override
		void close() {
		}

		@Override
		String toString() {
			return sourceDirectory.toString()
		}
	}

	static class Zip implements ModuleBundleSource {
		final File zip
		private ZipFile zipFile

		Zip(File zip) {
			this.zip = zip
		}

		@Override
		void init() {
			try {
				zipFile = new ZipFile(zip)
			} catch (Exception ex) {
				throw new IllegalArgumentException("Could not open module ZIP file: ${zip}", ex)
			}
		}

		@Override
		void processFiles(ModuleBundleSource.ModuleBundleFileHandler handler) {

			zipFile.entries().each { ZipEntry entry ->
				def contents = { zipFile.getInputStream(entry) }
				handler.handleFile(entry.name, contents)
			}
		}

		@Override
		void close() {
			zipFile.close()
		}

		@Override
		String toString() {
			return zip.toString()
		}
	}
}
