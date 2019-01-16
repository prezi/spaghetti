package com.prezi.spaghetti.bundle;

import com.prezi.spaghetti.bundle.internal.DefaultModuleBundle;
import com.prezi.spaghetti.bundle.internal.ModuleBundleInternal;
import com.prezi.spaghetti.bundle.internal.ModuleBundleParameters;
import com.prezi.spaghetti.structure.OutputType;
import com.prezi.spaghetti.structure.internal.StructuredDirectoryProcessor;
import com.prezi.spaghetti.structure.internal.StructuredDirectoryWriter;
import com.prezi.spaghetti.structure.internal.StructuredProcessor;
import com.prezi.spaghetti.structure.internal.StructuredZipProcessor;
import com.prezi.spaghetti.structure.internal.StructuredZipWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;

/**
 * Creates, loads and extracts module bundles.
 */
public final class ModuleBundleFactory {
	private static final Logger logger = LoggerFactory.getLogger(ModuleBundleFactory.class);

	/**
	 * Create a module bundle.
	 *
	 * @param type   whether to create a ZIP or an exploded directory bundle.
	 * @param output the location of the bundle.
	 * @param params the parameters for creating the bundle.
	 * @return the created bundle.
	 */
	public static ModuleBundle create(OutputType type, File output, ModuleBundleParameters params) throws IOException {
		switch (type) {
			case ZIP:
				return createZip(output, params);
			case DIRECTORY:
				return createDirectory(output, params);
			default:
				throw new AssertionError("Unknown type: " + type);
		}
	}

	/**
	 * Create a ZIP module bundle.
	 *
	 * @param outputFile the location of ZIP file to create.
	 * @param params     the parameters for creating the bundle.
	 * @return the created bundle.
	 */
	public static ModuleBundle createZip(File outputFile, ModuleBundleParameters params) throws IOException {
		return DefaultModuleBundle.create(new StructuredZipWriter(outputFile), params, false);
	}

	/**
	 * Create an exploded directory module bundle.
	 *
	 * @param outputDirectory the location of directory to create.
	 * @param params          the parameters for creating the bundle.
	 * @return the created bundle.
	 */
	public static ModuleBundle createDirectory(File outputDirectory, ModuleBundleParameters params) throws IOException {
		return DefaultModuleBundle.create(new StructuredDirectoryWriter(outputDirectory), params, false);
	}

	/**
	 * Loads an existing module bundle from a ZIP file or an exploded directory.
	 *
	 * @param input the location of the bundle.
	 * @return the loaded bundle.
	 */
	public static ModuleBundle load(final File input) throws IOException {
		if (!input.exists()) {
			throw new IllegalArgumentException("Module not found: " + String.valueOf(input));
		}

		StructuredProcessor source;
		if (input.isFile()) {
			logger.debug("{} is a file, trying to load as ZIP", input);
			source = new StructuredZipProcessor(input);
		} else if (input.isDirectory()) {
			logger.debug("{} is a directory, trying to load as exploded", input);
			source = new StructuredDirectoryProcessor(input);
		} else {
			throw new RuntimeException("Unknown module format: " + input);
		}

		source.init();
		try {
			return DefaultModuleBundle.loadInternal(source, false);
		} finally {
			source.close();
		}
	}

	/**
	 * Extracts a loaded module bundle into a directory.
	 *
	 * @param bundle          the bundle to extract.
	 * @param outputDirectory the directory to extract into.
	 * @param elements        the elements of the bundle to extract. If none is provided, all elements will be extracted.
	 */
	public static void extract(ModuleBundle bundle, File outputDirectory, ModuleBundleElement... elements) throws IOException {
		EnumSet<ModuleBundleElement> elemEnum;
		if (elements == null) {
			elemEnum = EnumSet.allOf(ModuleBundleElement.class);
		} else {
			elemEnum = EnumSet.copyOf(Arrays.asList(elements));
		}
		extract(bundle, outputDirectory, elemEnum);
	}

	/**
	 * Extracts a loaded module bundle into a directory.
	 *
	 * @param bundle          the bundle to extract.
	 * @param outputDirectory the directory to extract into.
	 * @param elements        the elements of the bundle to extract.
	 */
	public static void extract(ModuleBundle bundle, File outputDirectory, EnumSet<ModuleBundleElement> elements) throws IOException {
		StructuredDirectoryWriter output = new StructuredDirectoryWriter(outputDirectory);
		output.init();
		try {
			((ModuleBundleInternal) bundle).extract(output, elements);
		} finally {
			output.close();
		}
	}
}
