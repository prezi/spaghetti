package com.prezi.spaghetti.bundle

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Created by lptr on 15/05/14.
 */
abstract interface BundleBuilder extends BundleAppender {
	void init()
	BundleSource create()
	void close()

	static interface BundleAppender {
		void appendFile(String path, Closure writeContents)
		BundleAppender subAppender(String path)
	}

	abstract private static class AbstractBundleAppender implements BundleAppender {
		final BundleAppender subAppender(String path) {
			return new SubBuilder(this, path)
		}
	}

	final private static class Directory extends AbstractBundleAppender implements BundleBuilder {
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
		BundleSource create() {
			return new BundleSource.Directory(directory)
		}

		@Override
		void close() {
		}

		@Override
		String toString() {
			return "directory: " + directory
		}
	}

	final private static class Zip extends AbstractBundleAppender implements BundleBuilder {
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
		BundleSource create() {
			close()
			return new BundleSource.Zip(zipFile)
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

	final private static class SubBuilder extends AbstractBundleAppender {
		private final BundleAppender parent
		private final String subPath

		SubBuilder(BundleAppender parent, String subPath) {
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
