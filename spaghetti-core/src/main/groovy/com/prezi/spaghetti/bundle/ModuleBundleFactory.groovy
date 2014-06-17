package com.prezi.spaghetti.bundle

import com.prezi.spaghetti.structure.StructuredReader
import com.prezi.spaghetti.structure.StructuredWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ModuleBundleFactory {
	private static final Logger logger = LoggerFactory.getLogger(ModuleBundleFactory)

	static ModuleBundle createZip(File outputFile, ModuleBundleParameters params) {
		return DefaultModuleBundle.create(new StructuredWriter.Zip(outputFile), params)
	}

	static ModuleBundle createDirectory(File outputDirectory, ModuleBundleParameters params) {
		return DefaultModuleBundle.create(new StructuredWriter.Directory(outputDirectory), params)
	}

	static ModuleBundle load(File inputFile) {
		if (!inputFile.exists()) {
			throw new IllegalArgumentException("Module not found: ${inputFile}")
		}
		def source
		if (inputFile.file) {
			logger.debug "{} is a file, trying to load as ZIP", inputFile
			source = new StructuredReader.Zip(inputFile)
		} else if (inputFile.directory) {
			logger.debug "{} is a directory, trying to load as exploded", inputFile
			source = new StructuredReader.Directory(inputFile)
		} else {
			throw new RuntimeException("Unknown module format: ${inputFile}")
		}

		source.init()
		try {
			return DefaultModuleBundle.loadInternal(source)
		} finally {
			source.close()
		}
	}

	static void extract(ModuleBundle bundle, File outputDirectory, ModuleBundleElement... elements) {
		extract(bundle, outputDirectory, elements ? EnumSet.of(*elements) : EnumSet.allOf(ModuleBundleElement))
	}

	static void extract(ModuleBundle bundle, File outputDirectory, EnumSet<ModuleBundleElement> elements = EnumSet.allOf(ModuleBundleElement)) {
		def output = new StructuredWriter.Directory(outputDirectory)
		output.init()
		try {
			bundle.extract(output, elements)
		} finally {
			output.close()
		}
	}
}
