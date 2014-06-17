package com.prezi.spaghetti.structure;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public interface StructuredWriter extends StructuredAppender {
	void init() throws IOException;

	StructuredReader create() throws IOException;

	void close() throws IOException;

	static abstract class AbstractStructuredAppender implements StructuredAppender {
		public final StructuredAppender subAppender(String path) {
			return new SubBuilder(this, path);
		}

		@Override
		public void appendFile(String path, final InputStream contents) throws IOException {
			appendFile(path, new IOAction<OutputStream>() {
				@Override
				public void execute(OutputStream outputStream) throws IOException {
					try {
						IOUtils.copy(contents, outputStream);
					} finally {
						IOUtils.closeQuietly(contents);
					}
				}
			});
		}

		@Override
		public void appendFile(String path, File contents) throws IOException {
			InputStream input = new FileInputStream(contents);
			try {
				appendFile(path, input);
			} finally {
				input.close();
			}
		}

		@Override
		public void appendFile(String path, final CharSequence contents) throws IOException {
			appendFile(path, new IOAction<OutputStream>() {
				@Override
				public void execute(OutputStream outputStream) throws IOException {
					IOUtils.write(contents, outputStream, UTF_8);
				}
			});
		}
	}

	class Directory extends AbstractStructuredAppender implements StructuredWriter {
		public Directory(File directory) {
			this.directory = directory;
		}

		@Override
		public void init() throws IOException {
			FileUtils.deleteDirectory(directory);
			directory.mkdirs();
		}

		@Override
		public void appendFile(String path, IOAction<OutputStream> writeContents) throws IOException {
			File file = new File(directory, path);
			file.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(file);
			try {
				writeContents.execute(out);
			} finally {
				out.close();
			}
		}

		@Override
		public StructuredReader create() {
			return new StructuredReader.Directory(directory);
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

	final class Zip extends AbstractStructuredAppender implements StructuredWriter {
		public Zip(File zipFile) {
			this.zipFile = zipFile;
		}

		@Override
		public void init() throws IOException {
			zipFile.delete();
			zipFile.getParentFile().mkdirs();
			zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
		}

		@Override
		public void appendFile(String path, IOAction<OutputStream> writeContents) throws IOException {
			zipStream.putNextEntry(new ZipEntry(path));
			writeContents.execute(zipStream);
		}

		@Override
		public StructuredReader create() throws IOException {
			close();
			return new StructuredReader.Zip(zipFile);
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

	final class SubBuilder extends AbstractStructuredAppender {
		public SubBuilder(StructuredAppender parent, String subPath) {
			this.parent = parent;
			this.subPath = subPath;
		}

		@Override
		public void appendFile(String path, IOAction<OutputStream> writeContents) throws IOException {
			parent.appendFile(subPath + "/" + path, writeContents);
		}

		@Override
		public String toString() {
			return parent.toString() + "/" + subPath;
		}

		private final StructuredAppender parent;
		private final String subPath;
	}
}
