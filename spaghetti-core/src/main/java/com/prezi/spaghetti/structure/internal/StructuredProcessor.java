package com.prezi.spaghetti.structure.internal;

import java.io.IOException;

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
}
