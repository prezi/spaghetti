package com.prezi.spaghetti.bundle

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Created by lptr on 15/05/14.
 */
interface ModuleBundleBuilder {
	void init()
	void addEntry(String path, Closure writeContents)
	ModuleBundleSource create()
	void close()

	static class Directory implements ModuleBundleBuilder {
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
		void addEntry(String path, Closure writeContents) {
			def file = new File(directory, path)
			file.parentFile.mkdirs()
			file.withOutputStream { out -> writeContents(out) }
		}

		@Override
		ModuleBundleSource create() {
			return new ModuleBundleSource.Directory(directory)
		}

		@Override
		void close() {
		}
	}

	static class Zip implements ModuleBundleBuilder {
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
		void addEntry(String path, Closure writeContents) {
			zipStream.putNextEntry(new ZipEntry(path))
			writeContents(zipStream)
		}

		@Override
		ModuleBundleSource create() {
			close()
			return new ModuleBundleSource.Zip(zipFile)
		}

		@Override
		void close() {
			if (zipStream != null) {
				zipStream.close()
				zipStream = null
			}
		}
	}
}
