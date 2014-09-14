package com.prezi.spaghetti.bundle;

import com.prezi.spaghetti.bundle.internal.DefaultModuleBundle;
import com.prezi.spaghetti.structure.StructuredReader;
import com.prezi.spaghetti.structure.internal.StructuredDirectoryReader;
import com.prezi.spaghetti.structure.internal.StructuredDirectoryWriter;
import com.prezi.spaghetti.structure.internal.StructuredZipReader;
import com.prezi.spaghetti.structure.internal.StructuredZipWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;

public class ModuleBundleFactory {
	private static final Logger logger = LoggerFactory.getLogger(ModuleBundleFactory.class);

	public static ModuleBundle createZip(File outputFile, ModuleBundleParameters params) throws IOException {
		return DefaultModuleBundle.create(new StructuredZipWriter(outputFile), params);
	}

	public static ModuleBundle createDirectory(File outputDirectory, ModuleBundleParameters params) throws IOException {
		return DefaultModuleBundle.create(new StructuredDirectoryWriter(outputDirectory), params);
	}

	public static ModuleBundle load(final File inputFile) throws IOException {
		if (!inputFile.exists()) {
			throw new IllegalArgumentException("Module not found: " + String.valueOf(inputFile));
		}

		StructuredReader source;
		if (inputFile.isFile()) {
			logger.debug("{} is a file, trying to load as ZIP", inputFile);
			source = new StructuredZipReader(inputFile);
		} else if (inputFile.isDirectory()) {
			logger.debug("{} is a directory, trying to load as exploded", inputFile);
			source = new StructuredDirectoryReader(inputFile);
		} else {
			throw new RuntimeException("Unknown module format: " + inputFile);
		}

		source.init();
		try {
			return DefaultModuleBundle.loadInternal(source);
		} finally {
			source.close();
		}
	}

	public static void extract(ModuleBundle bundle, File outputDirectory, ModuleBundleElement... elements) throws IOException {
		EnumSet<ModuleBundleElement> elemEnum;
		if (elements == null) {
			elemEnum = EnumSet.allOf(ModuleBundleElement.class);
		} else {
			elemEnum = EnumSet.copyOf(Arrays.asList(elements));
		}
		extract(bundle, outputDirectory, elemEnum);
	}

	public static void extract(ModuleBundle bundle, File outputDirectory, EnumSet<ModuleBundleElement> elements) throws IOException {
		StructuredDirectoryWriter output = new StructuredDirectoryWriter(outputDirectory);
		output.init();
		try {
			bundle.extract(output, elements);
		} finally {
			output.close();
		}
	}

	public static void extract(ModuleBundle bundle, File outputDirectory) throws IOException {
		ModuleBundleFactory.extract(bundle, outputDirectory, EnumSet.allOf(ModuleBundleElement.class));
	}
}
