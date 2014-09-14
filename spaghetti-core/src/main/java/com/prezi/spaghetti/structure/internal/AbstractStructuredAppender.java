package com.prezi.spaghetti.structure.internal;

import com.google.common.base.Charsets;
import com.prezi.spaghetti.structure.IOAction;
import com.prezi.spaghetti.structure.StructuredAppender;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractStructuredAppender implements StructuredAppender {
	public final StructuredAppender subAppender(String path) {
		return new SubBuilder(this, path);
	}

	@Override
	public void appendFile(String path, final InputStream contents) throws IOException {
		appendFile(path, new IOAction<OutputStream>() {
			@Override
			public void execute(OutputStream outputStream) throws IOException {
				try {
					IOUtils.copy(contents, outputStream);
				} finally {
					IOUtils.closeQuietly(contents);
				}
			}
		});
	}

	@Override
	public void appendFile(String path, File contents) throws IOException {
		InputStream input = new FileInputStream(contents);
		try {
			appendFile(path, input);
		} finally {
			input.close();
		}
	}

	@Override
	public void appendFile(String path, final CharSequence contents) throws IOException {
		appendFile(path, new IOAction<OutputStream>() {
			@Override
			public void execute(OutputStream outputStream) throws IOException {
				IOUtils.write(contents, outputStream, Charsets.UTF_8);
			}
		});
	}

	private static final class SubBuilder extends AbstractStructuredAppender {
		public SubBuilder(StructuredAppender parent, String subPath) {
			this.parent = parent;
			this.subPath = subPath;
		}

		@Override
		public void appendFile(String path, IOAction<OutputStream> writeContents) throws IOException {
			parent.appendFile(subPath + "/" + path, writeContents);
		}

		@Override
		public String toString() {
			return parent.toString() + "/" + subPath;
		}

		private final StructuredAppender parent;
		private final String subPath;
	}
}
