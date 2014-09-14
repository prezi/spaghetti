package com.prezi.spaghetti.structure;

import java.io.IOException;
import java.io.InputStream;

public interface StructuredReader {
	void init() throws IOException;
	boolean hasFile(String path) throws IOException;
	void processFile(String path, FileHandler handler) throws IOException;
	void processFiles(FileHandler handler) throws IOException;
	void close() throws IOException;

	interface FileHandler {
		void handleFile(String path, IOCallable<? extends InputStream> contents) throws IOException;
	}
}
