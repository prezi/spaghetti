package com.prezi.spaghetti.bundle

import groovy.io.FileType

import java.util.concurrent.Callable
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Created by lptr on 15/05/14.
 */
public interface BundleSource {
	void init()
	boolean hasFile(String path)
	void processFile(String path, ModuleBundleFileHandler handler)
	void processFiles(ModuleBundleFileHandler handler)
	void close()

	interface ModuleBundleFileHandler {
		void handleFile(String path, Callable<? extends InputStream> contents)
	}

	static class Directory implements BundleSource {
		final File sourceDirectory

		Directory(File sourceDirectory) {
			this.sourceDirectory = sourceDirectory
		}

		@Override
		void init() {
		}

		@Override
		boolean hasFile(String path) {
			return new File(sourceDirectory, path).exists()
		}

		@Override
		void processFile(String path, ModuleBundleFileHandler handler) {
			def file = new File(sourceDirectory, path)
			if (!file.file) {
				throw new IllegalArgumentException("Could not find file in bundle: ${file}")
			}
			handleFile(handler, path, file)
		}

		@Override
		void processFiles(ModuleBundleFileHandler handler) {
			if (!sourceDirectory.exists()) {
				throw new IllegalArgumentException("Could not find module bundle directory: ${sourceDirectory}")
			}

			sourceDirectory.eachFile(FileType.FILES) { File file ->
				def path = sourceDirectory.toURI().relativize(file.toURI()).toString()
				handleFile(handler, path, file)
			}
		}

		private static handleFile(ModuleBundleFileHandler handler, String path, File file) {
			handler.handleFile(path, { new FileInputStream(file) })
		}

		@Override
		void close() {
		}

		@Override
		String toString() {
			return "directory: " + sourceDirectory.toString()
		}
	}

	static class Zip implements BundleSource {
		final File zip
		private ZipFile zipFile

		Zip(File zip) {
			this.zip = zip
		}

		@Override
		void init() {
			try {
				this.zipFile = new ZipFile(zip)
			} catch (Exception ex) {
				// Just so that IntelliJ is happy
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
			if (entry) {
				throw new IllegalArgumentException("Could not find file \"${path}\" in bundle: ${zip}")
			}
			handleEntry(handler, entry)
		}

		@Override
		void processFiles(ModuleBundleFileHandler handler) {
			zipFile.entries().each { ZipEntry entry ->
				handleEntry(handler, entry)
			}
		}

		private void handleEntry(ModuleBundleFileHandler handler, ZipEntry entry) {
			handler.handleFile(entry.name, { zipFile.getInputStream(entry) })
		}

		@Override
		void close() {
			zipFile.close()
		}

		@Override
		String toString() {
			return "zip:" + zip.toString()
		}
	}
}
