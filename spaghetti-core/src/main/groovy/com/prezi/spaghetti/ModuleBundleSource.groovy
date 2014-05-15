package com.prezi.spaghetti

import java.util.concurrent.Callable
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Created by lptr on 15/05/14.
 */
public interface ModuleBundleSource {
	void processFiles(ModuleBundleFileHandler handler)

	interface ModuleBundleFileHandler {
		void handleFile(String path, Callable<? extends InputStream> contents)
	}

	class Directory implements ModuleBundleSource {
		final File sourceDirectory

		Directory(File sourceDirectory) {
			this.sourceDirectory = sourceDirectory
		}

		@Override
		void processFiles(ModuleBundleSource.ModuleBundleFileHandler handler) {
			if (!sourceDirectory.exists()) {
				throw new IllegalArgumentException("Could not find module bundle directory: ${sourceDirectory}")
			}

			sourceDirectory.eachFile { File file ->
				def path = sourceDirectory.toURI().relativize(file.toURI()).toString()
				handler.handleFile(path, { new FileInputStream(file) })
			}
		}
	}

	class Zip implements ModuleBundleSource {
		final File zip

		Zip(File zip) {
			this.zip = zip
		}

		@Override
		void processFiles(ModuleBundleSource.ModuleBundleFileHandler handler) {
			ZipFile zipFile
			try {
				zipFile = new ZipFile(zip)
			} catch (Exception ex) {
				throw new IllegalArgumentException("Could not open module ZIP file: ${zip}", ex)
			}

			zipFile.entries().each { ZipEntry entry ->
				def contents = { zipFile.getInputStream(entry) }
				handler.handleFile(entry.name, contents)
			}
		}
	}
}
