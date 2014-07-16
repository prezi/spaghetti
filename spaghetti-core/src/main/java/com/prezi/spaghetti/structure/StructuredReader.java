package com.prezi.spaghetti.structure;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public interface StructuredReader {
	void init() throws IOException;

	boolean hasFile(String path) throws IOException;

	void processFile(String path, FileHandler handler) throws IOException;

	void processFiles(FileHandler handler) throws IOException;

	void close() throws IOException;

	interface FileHandler {
		void handleFile(String path, IOCallable<? extends InputStream> contents) throws IOException;
	}

	class Directory implements StructuredReader {
		private final File sourceDirectory;

		public Directory(File sourceDirectory) {
			this.sourceDirectory = sourceDirectory;
		}

		@Override
		public void init() {
		}

		@Override
		public boolean hasFile(String path) {
			return new File(sourceDirectory, path).exists();
		}

		@Override
		public void processFile(String path, FileHandler handler) throws IOException {
			final File file = new File(sourceDirectory, path);
			if (!file.isFile()) {
				throw new IllegalArgumentException("Could not find file in bundle: " + file);
			}

			handleFile(handler, path, file);
		}

		@Override
		public void processFiles(final FileHandler handler) throws IOException {
			if (!sourceDirectory.exists()) {
				throw new IllegalArgumentException("Could not find module bundle directory: " + String.valueOf(sourceDirectory));
			}

			for (File file : FileUtils.listFiles(sourceDirectory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
				String path = sourceDirectory.toURI().relativize(file.toURI()).toString();
				handleFile(handler, path, file);
			}
		}

		private static void handleFile(FileHandler handler, String path, final File file) throws IOException {
			handler.handleFile(path, new IOCallable<InputStream>() {
				@Override
				public InputStream call() throws IOException {
					return new FileInputStream(file);
				}
			});
		}

		@Override
		public void close() {
		}

		@Override
		public String toString() {
			return "directory: " + sourceDirectory.toString();
		}

		public final File getSourceDirectory() {
			return sourceDirectory;
		}
	}

	class Zip implements StructuredReader {
		private final File zip;
		private ZipFile zipFile;

		public Zip(File zip) {
			this.zip = zip;
		}

		@Override
		public void init() throws IOException {
			this.zipFile = new ZipFile(zip);
		}

		@Override
		public boolean hasFile(String path) {
			return zipFile.getEntry(path) != null;
		}

		@Override
		public void processFile(final String path, FileHandler handler) throws IOException {
			ZipEntry entry = zipFile.getEntry(path);
			if (entry == null) {
				throw new IllegalArgumentException("Could not find file \"" + path + "\" in bundle: " + zip);
			}

			handleEntry(handler, entry);
		}

		@Override
		public void processFiles(FileHandler handler) throws IOException {
			UnmodifiableIterator<? extends ZipEntry> entries = Iterators.forEnumeration(zipFile.entries());
			while (entries.hasNext()) {
				ZipEntry entry = entries.next();
				if (!entry.isDirectory()) {
					handleEntry(handler, entry);
				}
			}
		}

		private void handleEntry(FileHandler handler, final ZipEntry entry) throws IOException {
			handler.handleFile(entry.getName(), new IOCallable<InputStream>() {
				@Override
				public InputStream call() throws IOException {
					return zipFile.getInputStream(entry);
				}
			});
		}

		@Override
		public void close() throws IOException {
			zipFile.close();
		}

		@Override
		public String toString() {
			return "zip:" + zip.toString();
		}

		public final File getZip() {
			return zip;
		}
	}
}
