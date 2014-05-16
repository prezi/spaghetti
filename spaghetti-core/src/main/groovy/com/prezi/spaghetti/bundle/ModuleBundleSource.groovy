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
	boolean hasFile(String path)
	void processFile(String path, ModuleBundleFileHandler handler)
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
		boolean hasFile(String path) {
			return new File(sourceDirectory, path).exists()
		}

		@Override
		void processFile(String path, ModuleBundleFileHandler handler) {
			def file = new File(sourceDirectory, path)
			if (!file.exists()) {
				throw new IllegalArgumentException("Could not find \"${path}\" in module bundle directory: ${sourceDirectory}")
			}
			handleFile(handler, path, file)
		}

		@Override
		void processFiles(ModuleBundleSource.ModuleBundleFileHandler handler) {
			sourceDirectory.eachFile(FileType.FILES) { File file ->
				def path = sourceDirectory.toURI().relativize(file.toURI()).toString()
				handleFile(handler, path, file)
			}
		}

		private static void handleFile(ModuleBundleFileHandler handler, String path, File file) {
			handler.handleFile(path, { new FileInputStream(file) })
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
		boolean hasFile(String path) {
			return zipFile.getEntry(path) != null
		}

		@Override
		void processFile(String path, ModuleBundleFileHandler handler) {
			def entry = zipFile.getEntry(path)
			if (!entry) {
				throw new IllegalArgumentException("Could not find \"${path}\" in module bundle zip: ${zip}")
			}
			handleEntry(handler, entry)
		}

		@Override
		void processFiles(ModuleBundleFileHandler handler) {
			zipFile.entries().each { ZipEntry entry ->
				handleEntry(handler, entry)
			}
		}

		private handleEntry(ModuleBundleFileHandler handler, ZipEntry entry) {
			handler.handleFile(entry.name, { zipFile.getInputStream(entry) })
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
