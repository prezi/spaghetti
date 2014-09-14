package com.prezi.spaghetti.structure;

import java.io.IOException;

public interface StructuredWriter extends StructuredAppender {
	void init() throws IOException;
	StructuredReader create() throws IOException;
	void close() throws IOException;
}
