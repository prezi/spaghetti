package com.prezi.spaghetti.structure.internal;

import com.prezi.spaghetti.structure.IOAction;
import com.prezi.spaghetti.structure.StructuredProcessor;
import com.prezi.spaghetti.structure.StructuredWriter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StructuredDirectoryWriter extends AbstractStructuredAppender implements StructuredWriter {
	public StructuredDirectoryWriter(File directory) {
		this.directory = directory;
	}

	@Override
	public void init() throws IOException {
		FileUtils.deleteQuietly(directory);
		FileUtils.forceMkdir(directory);
	}

	@Override
	public void appendFile(String path, IOAction<OutputStream> contents) throws IOException {
		File file = new File(directory, path);
		FileUtils.forceMkdir(file.getParentFile());
		FileOutputStream out = new FileOutputStream(file);
		try {
			contents.execute(out);
		} finally {
			out.close();
		}
	}

	@Override
	public StructuredProcessor create() {
		return new StructuredDirectoryProcessor(directory);
	}

	@Override
	public void close() {
	}

	@Override
	public String toString() {
		return "directory: " + directory;
	}

	private final File directory;
}
