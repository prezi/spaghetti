package com.prezi.spaghetti.gradle.internal;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class TextFileUtils {
	public static Iterable<String> getText(Iterable<File> files) {
		return Iterables.transform(files, new Function<File, String>() {
			@Override
			public String apply(File file) {
				try {
					return getText(file);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public static String getText(File file) throws IOException {
		return Files.asCharSource(file, Charsets.UTF_8).read();
	}
}
