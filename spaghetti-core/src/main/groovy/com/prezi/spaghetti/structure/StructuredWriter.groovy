package com.prezi.spaghetti.structure

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Created by lptr on 15/05/14.
 */
abstract interface StructuredWriter extends StructuredAppender {
	void init()
	StructuredReader create()
	void close()

	static interface StructuredAppender {
		void appendFile(String path, Closure writeContents)
		StructuredAppender subAppender(String path)
	}

	abstract private static class AbstractStructuredAppender implements StructuredAppender {
		final StructuredAppender subAppender(String path) {
			return new SubBuilder(this, path)
		}
	}

	final private static class Directory extends AbstractStructuredAppender implements StructuredWriter {
		private final File directory

		Directory(File directory) {
			this.directory = directory
		}

		@Override
		void init() {
			directory.delete() || directory.deleteDir()
			directory.mkdirs()
		}

		@Override
		void appendFile(String path, Closure writeContents) {
			def file = new File(directory, path)
			file.parentFile.mkdirs()
			file.withOutputStream { out -> writeContents(out) }
		}

		@Override
		StructuredReader create() {
			return new StructuredReader.Directory(directory)
		}

		@Override
		void close() {
		}

		@Override
		String toString() {
			return "directory: " + directory
		}
	}

	final private static class Zip extends AbstractStructuredAppender implements StructuredWriter {
		File zipFile
		ZipOutputStream zipStream

		Zip(File zipFile) {
			this.zipFile = zipFile
		}

		@Override
		void init() {
			zipFile.delete()
			zipFile.parentFile.mkdirs()
			zipStream = new ZipOutputStream(new FileOutputStream(zipFile))
		}

		@Override
		void appendFile(String path, Closure writeContents) {
			zipStream.putNextEntry(new ZipEntry(path))
			writeContents(zipStream)
		}

		@Override
		StructuredReader create() {
			close()
			return new StructuredReader.Zip(zipFile)
		}

		@Override
		void close() {
			if (zipStream != null) {
				zipStream.close()
				zipStream = null
			}
		}

		@Override
		String toString() {
			return "zip: " + zipFile
		}
	}

	final private static class SubBuilder extends AbstractStructuredAppender {
		private final StructuredAppender parent
		private final String subPath

		SubBuilder(StructuredAppender parent, String subPath) {
			this.parent = parent
			this.subPath = subPath
		}

		@Override
		void appendFile(String path, Closure writeContents) {
			parent.appendFile("${subPath}/${path}", writeContents)
		}

		@Override
		String toString() {
			return parent.toString() + "/" + subPath
		}
	}
}
