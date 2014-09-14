package com.prezi.spaghetti.structure.internal;

import com.prezi.spaghetti.structure.IOCallable;
import com.prezi.spaghetti.structure.StructuredReader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StructuredDirectoryReader implements StructuredReader {
	private final File sourceDirectory;

	public StructuredDirectoryReader(File sourceDirectory) {
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
