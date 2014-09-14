package com.prezi.spaghetti.structure;

import java.io.IOException;
import java.io.InputStream;

/**
 * Processor for file structures.
 */
public interface StructuredProcessor {
	/**
	 * Initializes the processor.
	 */
	void init() throws IOException;

	/**
	 * Check if a certain path exists in the structure.
	 *
	 * @param path the path to check.
	 */
	boolean hasFile(String path) throws IOException;

	/**
	 * Process the file under {@code path}.
	 *
	 * @param path      the path to process.
	 * @param processor the processor to use.
	 */
	void processFile(String path, FileProcessor processor) throws IOException;

	/**
	 * Process all files in the structure.
	 *
	 * @param processor the file processor to use.
	 */
	void processFiles(FileProcessor processor) throws IOException;

	/**
	 * Close the processor.
	 */
	void close() throws IOException;

	/**
	 * Interface to process a file in a structure.
	 */
	interface FileProcessor {
		/**
		 * Process the file at path with the given contents.
		 *
		 * @param path     the path of the file.
		 * @param contents the contents of the file.
		 */
		void processFile(String path, IOCallable<? extends InputStream> contents) throws IOException;
	}
}
