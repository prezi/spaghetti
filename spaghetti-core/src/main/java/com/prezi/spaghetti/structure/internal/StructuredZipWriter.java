package com.prezi.spaghetti.structure.internal;

import com.prezi.spaghetti.structure.IOAction;
import com.prezi.spaghetti.structure.StructuredProcessor;
import com.prezi.spaghetti.structure.StructuredWriter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
* Created by lptr on 14/09/14.
*/
public final class StructuredZipWriter extends AbstractStructuredAppender implements StructuredWriter {
	public StructuredZipWriter(File zipFile) {
		this.zipFile = zipFile;
	}

	@Override
	public void init() throws IOException {
		FileUtils.deleteQuietly(zipFile);
		FileUtils.forceMkdir(zipFile.getParentFile());
		zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
	}

	@Override
	public void appendFile(String path, IOAction<OutputStream> contents) throws IOException {
		zipStream.putNextEntry(new ZipEntry(path));
		contents.execute(zipStream);
	}

	@Override
	public StructuredProcessor create() throws IOException {
		close();
		return new StructuredZipProcessor(zipFile);
	}

	@Override
	public void close() throws IOException {
		if (zipStream != null) {
			zipStream.close();
			zipStream = null;
		}

	}

	@Override
	public String toString() {
		return "zip: " + zipFile;
	}

	public File getZipFile() {
		return zipFile;
	}

	public void setZipFile(File zipFile) {
		this.zipFile = zipFile;
	}

	public ZipOutputStream getZipStream() {
		return zipStream;
	}

	public void setZipStream(ZipOutputStream zipStream) {
		this.zipStream = zipStream;
	}

	private File zipFile;
	private ZipOutputStream zipStream;
}
