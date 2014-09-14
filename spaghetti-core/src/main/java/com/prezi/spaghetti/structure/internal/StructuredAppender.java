package com.prezi.spaghetti.structure.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * An appender to create file structures.
 */
public interface StructuredAppender {
	/**
	 * Appends a file from an {@link IOAction}.
	 *
	 * @param path          location of the file in the structure.
	 * @param contents contents of the file.
	 */
	void appendFile(String path, IOAction<OutputStream> contents) throws IOException;

	/**
	 * Appends a file from another file.
	 *
	 * @param path     location of the file in the structure.
	 * @param contents contents of the file.
	 */
	void appendFile(String path, File contents) throws IOException;

	/**
	 * Appends a file from an {@link java.io.InputStream}. The stream is closed
	 * after its contents are appended to the structure.
	 *
	 * @param path     location of the file in the structure.
	 * @param contents contents of the file.
	 */
	void appendFile(String path, InputStream contents) throws IOException;

	/**
	 * Appends a file from a {@link java.lang.CharSequence}.
	 *
	 * @param path     location of the file in the structure.
	 * @param contents contents of the file.
	 */
	void appendFile(String path, CharSequence contents) throws IOException;

	/**
	 * Creates an appender for a sub-directory of this appender.
	 * @param path the path of the sub-directory
	 */
	StructuredAppender subAppender(String path) throws IOException;
}
