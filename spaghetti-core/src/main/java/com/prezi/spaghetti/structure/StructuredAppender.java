package com.prezi.spaghetti.structure;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StructuredAppender {
	void appendFile(String path, IOAction<OutputStream> writeContents) throws IOException;
	void appendFile(String path, File contents) throws IOException;
	void appendFile(String path, InputStream contents) throws IOException;
	void appendFile(String path, CharSequence contents) throws IOException;
	StructuredAppender subAppender(String path) throws IOException;
}
