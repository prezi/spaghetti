package com.prezi.spaghetti.structure.internal;

import com.prezi.spaghetti.structure.FileProcessor;
import com.prezi.spaghetti.structure.IOCallable;
import com.prezi.spaghetti.structure.StructuredProcessor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StructuredDirectoryProcessor implements StructuredProcessor {
	private final File sourceDirectory;

	public StructuredDirectoryProcessor(File sourceDirectory) {
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
	public void processFile(String path, FileProcessor processor) throws IOException {
		final File file = new File(sourceDirectory, path);
		if (!file.isFile()) {
			throw new IllegalArgumentException("Could not find file in bundle: " + file);
		}

		handleFile(processor, path, file);
	}

	@Override
	public void processFiles(final FileProcessor processor) throws IOException {
		if (!sourceDirectory.exists()) {
			throw new IllegalArgumentException("Could not find module bundle directory: " + String.valueOf(sourceDirectory));
		}

		for (File file : FileUtils.listFiles(sourceDirectory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
			String path = sourceDirectory.toURI().relativize(file.toURI()).toString();
			handleFile(processor, path, file);
		}
	}

	private static void handleFile(FileProcessor handler, String path, final File file) throws IOException {
		handler.processFile(path, new IOCallable<InputStream>() {
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
