package com.prezi.spaghetti.bundle;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.Version;
import com.prezi.spaghetti.structure.IOAction;
import com.prezi.spaghetti.structure.IOCallable;
import com.prezi.spaghetti.structure.StructuredAppender;
import com.prezi.spaghetti.structure.StructuredReader;
import com.prezi.spaghetti.structure.StructuredWriter;
import groovy.lang.Reference;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DefaultModuleBundle extends AbstractModuleBundle {
	private static final Logger logger = LoggerFactory.getLogger(DefaultModuleBundle.class);
	private static final Attributes.Name MANIFEST_ATTR_SPAGHETTI_VERSION = new Attributes.Name("Spaghetti-Version");
	private static final Attributes.Name MANIFEST_ATTR_SPAGHETTI_BUILD = new Attributes.Name("Spaghetti-Build");
	private static final Attributes.Name MANIFEST_ATTR_MODULE_NAME = new Attributes.Name("Module-Name");
	private static final Attributes.Name MANIFEST_ATTR_MODULE_VERSION = new Attributes.Name("Module-Version");
	private static final Attributes.Name MANIFEST_ATTR_MODULE_SOURCE = new Attributes.Name("Module-Source");
	private static final Attributes.Name MANIFEST_ATTR_MODULE_DEPENDENCIES = new Attributes.Name("Module-Dependencies");
	protected final StructuredReader source;

	protected DefaultModuleBundle(StructuredReader source, String name, String version, String sourceBaseUrl, Set<String> dependentModules, Set<String> resourcePaths) {
		super(name, version, sourceBaseUrl, dependentModules, resourcePaths);
		this.source = source;
	}

	@Override
	public String getDefinition() throws IOException {
		return getString(DEFINITION_PATH);
	}

	@Override
	public String getJavaScript() throws IOException {
		return getString(JAVASCRIPT_PATH);
	}

	@Override
	public String getSourceMap() throws IOException {
		return getString(SOURCE_MAP_PATH);
	}

	protected static DefaultModuleBundle create(final StructuredWriter builder, ModuleBundleParameters params) throws IOException {
		Preconditions.checkNotNull(params.name, "name");
		Preconditions.checkNotNull(params.version, "version");
		Preconditions.checkNotNull(params.definition, "definition");
		Preconditions.checkNotNull(params.javaScript, "javaScript");

		builder.init();
		try {
			Set<String> resourcePaths = Sets.newLinkedHashSet();

			// Store manifest
			final Manifest manifest = new Manifest();
			manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
			manifest.getMainAttributes().put(MANIFEST_ATTR_SPAGHETTI_VERSION, Version.SPAGHETTI_VERSION);
			manifest.getMainAttributes().put(MANIFEST_ATTR_SPAGHETTI_BUILD, Version.SPAGHETTI_BUILD);
			manifest.getMainAttributes().put(MANIFEST_ATTR_MODULE_NAME, params.name);
			String version = params.version;
			manifest.getMainAttributes().put(MANIFEST_ATTR_MODULE_VERSION, version != null ? version : "");
			String url = params.sourceBaseUrl;
			manifest.getMainAttributes().put(MANIFEST_ATTR_MODULE_SOURCE, url != null ? url : "");
			manifest.getMainAttributes().put(MANIFEST_ATTR_MODULE_DEPENDENCIES, Joiner.on(',').join(params.dependentModules));
			builder.appendFile(ModuleBundle.MANIFEST_MF_PATH, new IOAction<OutputStream>() {
				@Override
				public void execute(OutputStream out) throws IOException {
					manifest.write(out);
				}
			});

			// Store definition
			builder.appendFile(ModuleBundle.DEFINITION_PATH, params.definition);

			// Store module itself
			builder.appendFile(ModuleBundle.JAVASCRIPT_PATH, params.javaScript);

			// Store sourcemap
			if (params.sourceMap != null) {
				builder.appendFile(ModuleBundle.SOURCE_MAP_PATH, params.sourceMap);
			}

			// Store resources
			final File resourceDir = params.resourcesDirectory;
			if (resourceDir != null && resourceDir.exists()) {
				for (File resourceFile : FileUtils.listFiles(resourceDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
					String resourcePath = ModuleBundle.RESOURCES_PREFIX + resourceDir.toURI().relativize(resourceFile.toURI()).toString();
					logger.debug("Adding resource {}", resourcePath);
					builder.appendFile(resourcePath, resourceFile);
					resourcePaths.add(resourcePath);
				}
			}

			StructuredReader source = builder.create();
			return new DefaultModuleBundle(source, params.name, params.version, params.sourceBaseUrl, params.dependentModules, Collections.unmodifiableSet(resourcePaths));
		} finally {
			builder.close();
		}
	}

	protected static DefaultModuleBundle loadInternal(final StructuredReader source) throws IOException {
		if (!source.hasFile(ModuleBundle.MANIFEST_MF_PATH)) {
			throw new IllegalArgumentException("Not a module, missing manifest: " + source);
		}

		if (!source.hasFile(ModuleBundle.DEFINITION_PATH)) {
			throw new IllegalArgumentException("Not a module, missing definition: " + String.valueOf(source));
		}

		if (!source.hasFile(ModuleBundle.JAVASCRIPT_PATH)) {
			throw new IllegalArgumentException("Not a module, missing JavaScript: " + String.valueOf(source));
		}


		final Reference<Manifest> manifest = new Reference<Manifest>(null);
		final Set<String> resourcePaths = Sets.newLinkedHashSet();
		source.processFiles(new StructuredReader.FileHandler() {
			@Override
			public void handleFile(String path, IOCallable<? extends InputStream> contents) throws IOException {
				if (ModuleBundle.MANIFEST_MF_PATH.equals(path)) {
					try {
						manifest.set(new Manifest(contents.call()));
					} catch (IOException e) {
						throw e;
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} else {
					if (path.startsWith(ModuleBundle.RESOURCES_PREFIX)) {
						resourcePaths.add(path.substring(ModuleBundle.RESOURCES_PREFIX.length()));
					}
				}
			}
		});

		final String spaghettiVersion = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_SPAGHETTI_VERSION);
		if (spaghettiVersion == null) {
			throw new IllegalArgumentException("Not a module, module version missing from manifest: " + String.valueOf(source));
		}

		if (!isSpaghettiVersionSupported(spaghettiVersion)) {
			throw new IllegalArgumentException("Spaghetti version mismatch (should be 1.x), but was \"" + spaghettiVersion + "\"): " + String.valueOf(source));
		}

		String name = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_MODULE_NAME);

		String versionString = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_MODULE_VERSION);
		String version = versionString != null ? versionString : "unknown-version";

		String moduleSourceString = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_MODULE_SOURCE);
		String sourceUrl = moduleSourceString != null ? moduleSourceString : "unknown-source";

		String moduleDependenciesString = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_MODULE_DEPENDENCIES);
		Set<String> dependentModules = !Strings.isNullOrEmpty(moduleDependenciesString) ? Sets.newLinkedHashSet(Arrays.asList(moduleDependenciesString.split(","))) : Collections.<String>emptySet();
		return new DefaultModuleBundle(source, name, version, sourceUrl, dependentModules, Collections.unmodifiableSet(resourcePaths));
	}

	private String getString(String path) throws IOException {
		source.init();
		try {
			if (!source.hasFile(path)) {
				return null;
			}

			final Reference<String> text = new Reference<String>(null);
			source.processFile(path, new StructuredReader.FileHandler() {
				@Override
				public void handleFile(String path, IOCallable<? extends InputStream> contents) throws IOException {
					text.set(IOUtils.toString(contents.call(), UTF_8));
				}

			});
			return text.get();
		} finally {
			source.close();
		}
	}

	@Override
	public void extract(StructuredAppender output, EnumSet<ModuleBundleElement> elements) throws IOException {
		source.init();
		try {
			extract(getName(), source, output, elements);
		} finally {
			source.close();
		}
	}

	protected static void extract(final String name, StructuredReader source, final StructuredAppender output, final EnumSet<ModuleBundleElement> elements) throws IOException {
		source.processFiles(new StructuredReader.FileHandler() {
			@Override
			public void handleFile(String path, IOCallable<? extends InputStream> contents) throws IOException {
				//noinspection StatementWithEmptyBody
				if (path.equals(ModuleBundle.MANIFEST_MF_PATH)) {
					// Do not extract manifest
				} else if (path.equals(ModuleBundle.DEFINITION_PATH)) {
					if (elements.contains(ModuleBundleElement.definition)) {
						output.appendFile(name + ".def", contents.call());
					}
				} else if (path.equals(ModuleBundle.JAVASCRIPT_PATH)) {
					if (elements.contains(ModuleBundleElement.javascript)) {
						output.appendFile(name + ".js", contents.call());
					}
				} else if (path.equals(ModuleBundle.SOURCE_MAP_PATH)) {
					if (elements.contains(ModuleBundleElement.sourcemap)) {
						output.appendFile(name + ".js.map", contents.call());
					}
				} else {
					if (elements.contains(ModuleBundleElement.resources) && path.startsWith(ModuleBundle.RESOURCES_PREFIX)) {
						String resourcePath = path.substring(ModuleBundle.RESOURCES_PREFIX.length());
						// Skip the resources directory itself
						if (!Strings.isNullOrEmpty(resourcePath)) {
							List<String> dirs = Lists.newArrayList(resourcePath.split("/"));
							String fileName = dirs.remove(dirs.size() - 1);
							StructuredAppender dirOutput = output;
							for (String dir : dirs) {
								dirOutput = dirOutput.subAppender(dir);
							}
							dirOutput.appendFile(fileName, contents.call());
						}
					}
				}
			}
		});
	}

	private static boolean isSpaghettiVersionSupported(String spaghettiVersion) {
		return spaghettiVersion.startsWith("2.");
	}
}
