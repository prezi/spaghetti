package com.prezi.spaghetti.structure;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface to process a file in a structure.
 */
public interface FileProcessor {
	/**
	 * Process the file at path with the given contents.
	 *
	 * @param path     the path of the file.
	 * @param contents the contents of the file.
	 */
	void processFile(String path, IOCallable<? extends InputStream> contents) throws IOException;
}
