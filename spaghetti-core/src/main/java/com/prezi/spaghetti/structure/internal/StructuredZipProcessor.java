package com.prezi.spaghetti.structure.internal;

import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.prezi.spaghetti.structure.IOCallable;
import com.prezi.spaghetti.structure.StructuredProcessor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class StructuredZipProcessor implements StructuredProcessor {
	private final File zip;
	private ZipFile zipFile;

	public StructuredZipProcessor(File zip) {
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
	public void processFile(final String path, FileProcessor processor) throws IOException {
		ZipEntry entry = zipFile.getEntry(path);
		if (entry == null) {
			throw new IllegalArgumentException("Could not find file \"" + path + "\" in bundle: " + zip);
		}

		handleEntry(processor, entry);
	}

	@Override
	public void processFiles(FileProcessor processor) throws IOException {
		UnmodifiableIterator<? extends ZipEntry> entries = Iterators.forEnumeration(zipFile.entries());
		while (entries.hasNext()) {
			ZipEntry entry = entries.next();
			if (!entry.isDirectory()) {
				handleEntry(processor, entry);
			}
		}
	}

	private void handleEntry(FileProcessor handler, final ZipEntry entry) throws IOException {
		handler.processFile(entry.getName(), new IOCallable<InputStream>() {
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
