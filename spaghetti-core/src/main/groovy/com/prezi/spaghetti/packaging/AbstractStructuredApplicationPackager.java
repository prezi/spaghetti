package com.prezi.spaghetti.packaging;

import com.google.common.collect.Maps;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleElement;
import com.prezi.spaghetti.structure.IOAction;
import com.prezi.spaghetti.structure.StructuredAppender;
import com.prezi.spaghetti.structure.StructuredWriter;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractStructuredApplicationPackager extends AbstractApplicationPackager {
	protected final Wrapper wrapper;

	public AbstractStructuredApplicationPackager(Wrapper wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	public void packageApplicationInternal(StructuredWriter writer, final ApplicationPackageParameters params) throws IOException {
		StructuredAppender modulesAppender = writer.subAppender(params.modulesDirectory);

		for (ModuleBundle bundle : params.bundles) {
			// Extract resources
			StructuredAppender moduleAppender = modulesAppender.subAppender(bundle.getName());
			bundle.extract(moduleAppender, EnumSet.of(ModuleBundleElement.resources, ModuleBundleElement.sourcemap));

			// Add JavaScript
			String wrappedJavaScript = wrapper.wrap(bundle.getName(), bundle.getDependentModules(), bundle.getJavaScript());
			String moduleFile = getModuleFileName(bundle);
			moduleAppender.appendFile(moduleFile, wrappedJavaScript);
		}

		// Add application
		final Map<String, Set<String>> dependencyTree = Maps.newLinkedHashMap();
		for (ModuleBundle bundle : params.bundles) {
			dependencyTree.put(bundle.getName(), bundle.getDependentModules());
		}

		writer.appendFile(params.applicationName, new IOAction<OutputStream>() {
			@Override
			public void execute(OutputStream out) throws IOException {
				for (String prefix : params.prefixes) {
					IOUtils.write(prefix, out, "utf-8");
				}

				String wrappedApplication = wrapper.makeApplication(params.baseUrl, params.modulesDirectory, dependencyTree, params.mainModule, params.execute);

				IOUtils.write(wrappedApplication, out, "utf-8");
				for (String suffix : params.suffixes) {
					IOUtils.write(suffix, out, "utf-8");
				}
			}
		});
	}

	protected abstract String getModuleFileName(ModuleBundle bundle);
}
