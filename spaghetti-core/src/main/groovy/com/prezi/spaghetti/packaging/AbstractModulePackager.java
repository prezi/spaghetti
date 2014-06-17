package com.prezi.spaghetti.packaging;

import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleElement;
import com.prezi.spaghetti.structure.IOAction;
import com.prezi.spaghetti.structure.StructuredWriter;
import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;

public abstract class AbstractModulePackager implements ModulePackager {
	protected final Wrapper wrapper;

	public AbstractModulePackager(Wrapper wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	public void packageModuleDirectory(File outputDirectory, ModulePackageParameters params) throws IOException {
		packageModule(new StructuredWriter.Directory(outputDirectory), params);
	}

	@Override
	public void packageModuleZip(File outputFile, ModulePackageParameters params) throws IOException {
		packageModule(new StructuredWriter.Zip(outputFile), params);
	}

	protected void packageModule(StructuredWriter writer, final ModulePackageParameters params) throws IOException {
		writer.init();
		try {
			final ModuleBundle bundle = params.bundle;
			EnumSet<ModuleBundleElement> elements = params.elements.clone();
			DefaultGroovyMethods.removeAll(elements, new Object[]{ModuleBundleElement.javascript, ModuleBundleElement.sourcemap});
			bundle.extract(writer, elements);
			if (params.elements.contains(ModuleBundleElement.javascript)) {
				writer.appendFile(getModuleName(bundle), new IOAction<OutputStream>() {
					@Override
					public void execute(OutputStream out) throws IOException {
						for (String prefix : params.prefixes) {
							IOUtils.write(prefix, out, "utf-8");
						}

						String wrappedModule = wrapper.wrap(bundle.getName(), bundle.getDependentModules(), bundle.getJavaScript());
						IOUtils.write(wrappedModule, out, "utf-8");

						for (String suffix : params.suffixes) {
							IOUtils.write(suffix, out, "utf-8");
						}
					}
				});
			}

			if (params.elements.contains(ModuleBundleElement.sourcemap)) {
				String sourceMap = bundle.getSourceMap();
				if (DefaultGroovyMethods.asBoolean(sourceMap)) {
					writer.appendFile(getModuleName(bundle) + ".map", sourceMap);
				}
			}
		} finally {
			writer.close();
		}
	}
}
