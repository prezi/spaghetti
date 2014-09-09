package com.prezi.spaghetti.packaging;

import com.google.common.base.Charsets;
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
	protected final StructuredWrapper wrapper;

	public AbstractStructuredApplicationPackager(StructuredWrapper wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	public void packageApplicationInternal(StructuredWriter writer, final ApplicationPackageParameters params) throws IOException {
		StructuredAppender modulesAppender = writer.subAppender(wrapper.getModulesDirectory());

		for (ModuleBundle bundle : params.bundles) {
			// Extract resources
			StructuredAppender moduleAppender = modulesAppender.subAppender(bundle.getName());
			bundle.extract(moduleAppender, EnumSet.of(ModuleBundleElement.resources, ModuleBundleElement.sourcemap));

			// Add JavaScript
			String wrappedJavaScript = wrapper.wrap(new ModuleWrappingParameters(bundle));
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
					IOUtils.write(prefix, out, Charsets.UTF_8);
				}

				String wrappedApplication = wrapper.makeApplication(dependencyTree, params.mainModule, params.execute);

				IOUtils.write(wrappedApplication, out, Charsets.UTF_8);
				for (String suffix : params.suffixes) {
					IOUtils.write(suffix, out, Charsets.UTF_8);
				}
			}
		});
	}

	protected abstract String getModuleFileName(ModuleBundle bundle);
}
