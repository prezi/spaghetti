package com.prezi.spaghetti.structure.internal;

import java.io.IOException;

/**
 * A writer that can create file structures.
 */
public interface StructuredWriter extends StructuredAppender {
	/**
	 * Initializes the writer.
	 */
	void init() throws IOException;

	/**
	 * Closes the writer and creates a reader to read it.
	 */
	StructuredProcessor create() throws IOException;

	/**
	 * Closes the writer.
	 */
	void close() throws IOException;
}
