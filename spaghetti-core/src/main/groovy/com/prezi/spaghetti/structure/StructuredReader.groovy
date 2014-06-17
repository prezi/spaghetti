package com.prezi.spaghetti.structure

import groovy.io.FileType

import java.util.concurrent.Callable
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

public interface StructuredReader {
	void init()
	boolean hasFile(String path)
	void processFile(String path, FileHandler handler)
	void processFiles(FileHandler handler)
	void close()

	interface FileHandler {
		void handleFile(String path, Callable<? extends InputStream> contents)
	}

	static class Directory implements StructuredReader {
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
		void processFile(String path, FileHandler handler) {
			def file = new File(sourceDirectory, path)
			if (!file.file) {
				throw new IllegalArgumentException("Could not find file in bundle: ${file}")
			}
			handleFile(handler, path, file)
		}

		@Override
		void processFiles(FileHandler handler) {
			if (!sourceDirectory.exists()) {
				throw new IllegalArgumentException("Could not find module bundle directory: ${sourceDirectory}")
			}

			sourceDirectory.eachFileRecurse(FileType.FILES) { File file ->
				def path = sourceDirectory.toURI().relativize(file.toURI()).toString()
				handleFile(handler, path, file)
			}
		}

		private static handleFile(FileHandler handler, String path, File file) {
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

	static class Zip implements StructuredReader {
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
		void processFile(String path, FileHandler handler) {
			def entry = zipFile.getEntry(path)
			if (!entry) {
				throw new IllegalArgumentException("Could not find file \"${path}\" in bundle: ${zip}")
			}
			handleEntry(handler, entry)
		}

		@Override
		void processFiles(FileHandler handler) {
			zipFile.entries().each { ZipEntry entry ->
				if (!entry.directory) {
					handleEntry(handler, entry)
				}
			}
		}

		private void handleEntry(FileHandler handler, ZipEntry entry) {
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
