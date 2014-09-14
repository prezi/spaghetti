package com.prezi.spaghetti.packaging.internal;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.prezi.spaghetti.bundle.ModuleBundleElement;
import com.prezi.spaghetti.bundle.internal.ModuleBundleInternal;
import com.prezi.spaghetti.packaging.ModulePackageParameters;
import com.prezi.spaghetti.packaging.ModulePackager;
import com.prezi.spaghetti.packaging.ModuleWrapper;
import com.prezi.spaghetti.packaging.ModuleWrapperParameters;
import com.prezi.spaghetti.structure.internal.IOAction;
import com.prezi.spaghetti.structure.internal.StructuredWriter;
import com.prezi.spaghetti.structure.internal.StructuredDirectoryWriter;
import com.prezi.spaghetti.structure.internal.StructuredZipWriter;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.EnumSet;

public abstract class AbstractModulePackager implements ModulePackager {
	protected final ModuleWrapper wrapper;

	public AbstractModulePackager(ModuleWrapper wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	public void packageModuleDirectory(File outputDirectory, ModulePackageParameters params) throws IOException {
		packageModule(new StructuredDirectoryWriter(outputDirectory), params);
	}

	@Override
	public void packageModuleZip(File outputFile, ModulePackageParameters params) throws IOException {
		packageModule(new StructuredZipWriter(outputFile), params);
	}

	protected void packageModule(StructuredWriter writer, final ModulePackageParameters params) throws IOException {
		writer.init();
		try {
			final ModuleBundleInternal bundle = (ModuleBundleInternal) params.bundle;
			EnumSet<ModuleBundleElement> elements = params.elements.clone();
			elements.removeAll(Arrays.asList(ModuleBundleElement.JAVASCRIPT, ModuleBundleElement.SOURCE_MAP));
			bundle.extract(writer, elements);
			if (params.elements.contains(ModuleBundleElement.JAVASCRIPT)) {
				writer.appendFile(getModuleName(bundle), new IOAction<OutputStream>() {
					@Override
					public void execute(OutputStream out) throws IOException {
						for (String prefix : params.prefixes) {
							IOUtils.write(prefix, out, Charsets.UTF_8);
						}

						String wrappedModule = wrapper.wrap(new ModuleWrapperParameters(bundle));
						IOUtils.write(wrappedModule, out, Charsets.UTF_8);

						for (String suffix : params.suffixes) {
							IOUtils.write(suffix, out, Charsets.UTF_8);
						}
					}
				});
			}

			if (params.elements.contains(ModuleBundleElement.SOURCE_MAP)) {
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
