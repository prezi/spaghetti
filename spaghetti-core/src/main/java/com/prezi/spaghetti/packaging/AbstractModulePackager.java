package com.prezi.spaghetti.packaging;

import com.google.common.base.Strings;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleElement;
import com.prezi.spaghetti.structure.IOAction;
import com.prezi.spaghetti.structure.StructuredWriter;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.EnumSet;

import static java.nio.charset.StandardCharsets.UTF_8;

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
			elements.removeAll(Arrays.asList(ModuleBundleElement.javascript, ModuleBundleElement.sourcemap));
			bundle.extract(writer, elements);
			if (params.elements.contains(ModuleBundleElement.javascript)) {
				writer.appendFile(getModuleName(bundle), new IOAction<OutputStream>() {
					@Override
					public void execute(OutputStream out) throws IOException {
						for (String prefix : params.prefixes) {
							IOUtils.write(prefix, out, UTF_8);
						}

						String wrappedModule = wrapper.wrap(bundle.getName(), bundle.getDependentModules(), bundle.getJavaScript());
						IOUtils.write(wrappedModule, out, UTF_8);

						for (String suffix : params.suffixes) {
							IOUtils.write(suffix, out, UTF_8);
						}
					}
				});
			}

			if (params.elements.contains(ModuleBundleElement.sourcemap)) {
				String sourceMap = bundle.getSourceMap();
				if (!Strings.isNullOrEmpty(sourceMap)) {
					writer.appendFile(getModuleName(bundle) + ".map", sourceMap);
				}
			}
		} finally {
			writer.close();
		}
	}
}
