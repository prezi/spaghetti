package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.structure.StructuredWriter

/**
 * Created by lptr on 25/05/14.
 */
abstract class AbstractApplicationPackager implements ApplicationPackager {
	@Override
	public void packageApplicationDirectory(File outputDirectory, ApplicationPackageParameters params) {
		packageApplication(new StructuredWriter.Directory(outputDirectory), params)
	}

	@Override
	public void packageApplicationZip(File outputFile, ApplicationPackageParameters params) {
		packageApplication(new StructuredWriter.Zip(outputFile), params)
	}

	protected void packageApplication(StructuredWriter writer, ApplicationPackageParameters params) {
		if (params.execute && !params.mainModule) {
			throw new IllegalArgumentException("Main bundle not set, but execute is")
		}
		if (params.mainModule && !params.bundles*.name.contains(params.mainModule)) {
			throw new IllegalArgumentException("Main bundle \"${params.mainModule}\" not found among bundles: ${params.bundles*.name.join(", ")}")
		}

		writer.init()
		try {
			packageApplicationInternal(writer, params)
		} finally {
			writer.close()
		}
	}

	abstract void packageApplicationInternal(StructuredWriter writer, ApplicationPackageParameters params)
}
