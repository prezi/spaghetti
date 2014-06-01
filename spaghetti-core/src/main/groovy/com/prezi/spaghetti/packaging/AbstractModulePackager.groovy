package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.bundle.ModuleBundleElement
import com.prezi.spaghetti.structure.StructuredWriter

/**
 * Created by lptr on 27/05/14.
 */
abstract class AbstractModulePackager implements ModulePackager {
	protected final Wrapper wrapper

	AbstractModulePackager(Wrapper wrapper) {
		this.wrapper = wrapper
	}

	@Override
	void packageModuleDirectory(File outputDirectory, ModulePackageParameters params) {
		packageModule(new StructuredWriter.Directory(outputDirectory), params)
	}

	@Override
	void packageModuleZip(File outputFile, ModulePackageParameters  params) {
		packageModule(new StructuredWriter.Zip(outputFile), params)
	}

	protected void packageModule(StructuredWriter writer, ModulePackageParameters params) {
		writer.init()
		try {
			def bundle = params.bundle
			def elements = params.elements.clone()
			elements.removeAll(ModuleBundleElement.javascript, ModuleBundleElement.sourcemap)
			bundle.extract(writer, elements)
			if (params.elements.contains(ModuleBundleElement.javascript)) {
				writer.appendFile(getModuleName(bundle), { out ->
					params.prefixes.each { out << it }
					out << wrapper.wrap(bundle.name, bundle.dependentModules, bundle.javaScript)
					params.suffixes.each { out << it }
				})
			}
			if (params.elements.contains(ModuleBundleElement.sourcemap)) {
				def sourceMap = bundle.sourceMap
				if (sourceMap) {
					writer.appendFile(getModuleName(bundle) + ".map", { out ->
						out << sourceMap
					})
				}
			}
		} finally {
			writer.close()
		}
	}
}
