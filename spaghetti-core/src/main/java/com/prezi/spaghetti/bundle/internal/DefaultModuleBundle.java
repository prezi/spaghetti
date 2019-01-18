package com.prezi.spaghetti.bundle.internal;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.DefinitionLanguage;
import com.prezi.spaghetti.bundle.ModuleBundleElement;
import com.prezi.spaghetti.bundle.ModuleBundleType;
import com.prezi.spaghetti.bundle.ModuleFormat;
import com.prezi.spaghetti.internal.Version;
import com.prezi.spaghetti.packaging.ModuleWrapperParameters;
import com.prezi.spaghetti.packaging.internal.UmdModuleWrapper;
import com.prezi.spaghetti.structure.internal.FileProcessor;
import com.prezi.spaghetti.structure.internal.IOAction;
import com.prezi.spaghetti.structure.internal.IOCallable;
import com.prezi.spaghetti.structure.internal.StructuredAppender;
import com.prezi.spaghetti.structure.internal.StructuredProcessor;
import com.prezi.spaghetti.structure.internal.StructuredWriter;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class DefaultModuleBundle extends AbstractModuleBundle {
	/**
	 * Path of the module definition inside the bundle.
	 */
	private static final String DEFINITION_PATH = "module.def";

	/**
	 * Path of the source map inside the bundle.
	 */
	private static final String SOURCE_MAP_PATH = "module.map";

	/**
	 * Path of the module's JavaScript code inside the bundle.
	 */
	private static final String JAVASCRIPT_PATH = "module.js";

	/**
	 * Path of the metadata file inside the bundle.
	 */
	private static final String MANIFEST_MF_PATH = "META-INF/MANIFEST.MF";

	/**
	 * Path of the resources directory inside the bundle.
	 */
	private static final String RESOURCES_PREFIX = "resources/";

	private static final Logger logger = LoggerFactory.getLogger(DefaultModuleBundle.class);
	private static final String[] SUPPORTED_VERSIONS_PREFIX = new String[]{"3.", "4.", "5.", "6.", "7.", "8.", "9.", "10.", "11.", "12."};
	private static final Attributes.Name MANIFEST_ATTR_SPAGHETTI_VERSION = new Attributes.Name("Spaghetti-Version");
	private static final Attributes.Name MANIFEST_ATTR_MODULE_NAME = new Attributes.Name("Module-Name");
	private static final Attributes.Name MANIFEST_ATTR_MODULE_VERSION = new Attributes.Name("Module-Version");
	private static final Attributes.Name MANIFEST_ATTR_MODULE_FORMAT = new Attributes.Name("Module-Format");
	private static final Attributes.Name MANIFEST_ATTR_MODULE_LAZY_LOADABLE = new Attributes.Name("Module-Lazy-Loadable");
	private static final Attributes.Name MANIFEST_ATTR_MODULE_SOURCE = new Attributes.Name("Module-Source");
	private static final Attributes.Name MANIFEST_ATTR_DEFINITION_LANGUAGE = new Attributes.Name("Definition-Language");
	private static final Attributes.Name MANIFEST_ATTR_MODULE_DEPENDENCIES = new Attributes.Name("Module-Dependencies");
	private static final Attributes.Name MANIFEST_ATTR_LAZY_DEPENDENCIES = new Attributes.Name("Lazy-Dependencies");
	private static final Attributes.Name MANIFEST_ATTR_EXTERNAL_DEPENDENCIES = new Attributes.Name("External-Dependencies");
	protected final StructuredProcessor source;

	protected DefaultModuleBundle(StructuredProcessor source, String name, String version, ModuleFormat format, DefinitionLanguage definitionLang, String sourceBaseUrl, Set<String> dependentModules, Set<String> lazyDependentModules, Map<String, String> externalDependencies, Set<String> resourcePaths, Boolean lazyLoadable) {
		super(name, version, format, definitionLang, sourceBaseUrl, dependentModules, lazyDependentModules, externalDependencies, resourcePaths, lazyLoadable);
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

	public static DefaultModuleBundle create(final StructuredWriter builder, ModuleBundleParameters params) throws IOException {
		Preconditions.checkNotNull(params.name, "name");
		Preconditions.checkNotNull(params.version, "version");
		Preconditions.checkNotNull(params.definition, "definition");
		if (ModuleBundleType.SOURCE_AND_DEFINITION.equals(params.moduleBundleType)) {
			Preconditions.checkNotNull(params.javaScript, "javaScript");
		}

		if (!isSpaghettiVersionSupported(Version.SPAGHETTI_VERSION)) {
			throw new IllegalArgumentException(
					String.format("Creating a bundle which Spaghetti does not support (\"%s\" should be included in %s).",
							Version.SPAGHETTI_VERSION,
							Arrays.toString(SUPPORTED_VERSIONS_PREFIX)
					)
			);
		}

		builder.init();
		try {
			Set<String> resourcePaths = Sets.newLinkedHashSet();

			// Store manifest
			final Manifest manifest = new Manifest();
			manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
			manifest.getMainAttributes().put(MANIFEST_ATTR_SPAGHETTI_VERSION, Version.SPAGHETTI_VERSION);
			manifest.getMainAttributes().put(MANIFEST_ATTR_MODULE_NAME, params.name);
			String version = params.version;
			manifest.getMainAttributes().put(MANIFEST_ATTR_MODULE_VERSION, version != null ? version : "");
			manifest.getMainAttributes().put(MANIFEST_ATTR_MODULE_FORMAT, params.format.name());
			manifest.getMainAttributes().put(MANIFEST_ATTR_DEFINITION_LANGUAGE, params.definitionLang.name());
			String url = params.sourceBaseUrl;
			manifest.getMainAttributes().put(MANIFEST_ATTR_MODULE_SOURCE, url != null ? url : "");
			manifest.getMainAttributes().put(MANIFEST_ATTR_MODULE_DEPENDENCIES,
					Joiner.on(',').join(params.dependentModules));
			manifest.getMainAttributes().put(MANIFEST_ATTR_LAZY_DEPENDENCIES,
					Joiner.on(',').join(params.lazyDependentModules));
			manifest.getMainAttributes().put(MANIFEST_ATTR_MODULE_LAZY_LOADABLE, params.lazyLoadable.toString());
			manifest.getMainAttributes().put(MANIFEST_ATTR_EXTERNAL_DEPENDENCIES,
					Joiner.on(',').withKeyValueSeparator(":").join(params.externalDependencies));
			builder.appendFile(MANIFEST_MF_PATH, new IOAction<OutputStream>() {
				@Override
				public void execute(OutputStream out) throws IOException {
					manifest.write(out);
				}
			});

			// Store definition
			builder.appendFile(DEFINITION_PATH, params.definition);

			if (params.javaScript != null) {

				// Store module itself
				if (params.format == ModuleFormat.Wrapperless) {
					builder.appendFile(JAVASCRIPT_PATH, params.javaScript);
				} else {
					String javaScript = new UmdModuleWrapper().wrap(
							new ModuleWrapperParameters(
									params.name,
									params.version,
									params.javaScript,
									params.dependentModules,
									params.lazyDependentModules,
									params.externalDependencies)
					);
					builder.appendFile(JAVASCRIPT_PATH, javaScript);
				}

				// Store sourcemap
				if (params.sourceMap != null) {
					builder.appendFile(SOURCE_MAP_PATH, params.sourceMap);
				}

				// Store resources
				final File resourceDir = params.resourcesDirectory;
				if (resourceDir != null && resourceDir.exists()) {
					for (File resourceFile : FileUtils.listFiles(resourceDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
						String resourcePath = RESOURCES_PREFIX + resourceDir.toURI().relativize(resourceFile.toURI()).toString();
						logger.debug("Adding resource {}", resourcePath);
						builder.appendFile(resourcePath, resourceFile);
						resourcePaths.add(resourcePath);
					}
				}
			}

			StructuredProcessor source = builder.create();
			return new DefaultModuleBundle(source, params.name, params.version, params.format, params.definitionLang, params.sourceBaseUrl, params.dependentModules, params.lazyDependentModules, params.externalDependencies, Collections.unmodifiableSet(resourcePaths), params.lazyLoadable);
		} finally {
			builder.close();
		}
	}

	public static DefaultModuleBundle loadInternal(final StructuredProcessor source, ModuleBundleType moduleBundleType) throws IOException {
		if (!source.hasFile(MANIFEST_MF_PATH)) {
			throw new IllegalArgumentException("Not a module, missing manifest: " + source);
		}

		if (!source.hasFile(DEFINITION_PATH)) {
			throw new IllegalArgumentException("Not a module, missing definition: " + String.valueOf(source));
		}

		if (ModuleBundleType.SOURCE_AND_DEFINITION.equals(moduleBundleType) && !source.hasFile(JAVASCRIPT_PATH)) {
			throw new IllegalArgumentException("Not a module, missing JavaScript: " + String.valueOf(source));
		}


		final AtomicReference<Manifest> manifest = new AtomicReference<Manifest>(null);
		final Set<String> resourcePaths = Sets.newLinkedHashSet();
		source.processFiles(new FileProcessor() {
			@Override
			public void processFile(String path, IOCallable<? extends InputStream> contents) throws IOException {
				if (MANIFEST_MF_PATH.equals(path)) {
					try {
						manifest.set(new Manifest(contents.call()));
					} catch (IOException e) {
						throw e;
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				} else {
					if (path.startsWith(RESOURCES_PREFIX)) {
						resourcePaths.add(path.substring(RESOURCES_PREFIX.length()));
					}
				}
			}
		});

		final String spaghettiVersion = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_SPAGHETTI_VERSION);
		if (spaghettiVersion == null) {
			throw new IllegalArgumentException("Not a module, module version missing from manifest: " + String.valueOf(source));
		}

		if (!isSpaghettiVersionSupported(spaghettiVersion)) {
			throw new IllegalArgumentException(
					String.format("Spaghetti version mismatch (should be any of %s, but was \"%s\"): %s",
							Arrays.toString(SUPPORTED_VERSIONS_PREFIX),
							spaghettiVersion,
							String.valueOf(source)
					)
			);
		}

		String name = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_MODULE_NAME);

		String versionString = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_MODULE_VERSION);
		String version = versionString != null ? versionString : "unknown-version";

		String formatString = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_MODULE_FORMAT);
		ModuleFormat format = formatString != null ? ModuleFormat.valueOf(formatString) : ModuleFormat.Wrapperless;

		String definitionLangString = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_DEFINITION_LANGUAGE);
		DefinitionLanguage definitionLang = definitionLangString != null ? DefinitionLanguage.valueOf(definitionLangString) : DefinitionLanguage.Spaghetti;

		String moduleSourceString = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_MODULE_SOURCE);
		String sourceUrl = moduleSourceString != null ? moduleSourceString : "unknown-source";

		String moduleDependenciesString = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_MODULE_DEPENDENCIES);
		Set<String> dependentModules = !Strings.isNullOrEmpty(moduleDependenciesString) ? Sets.newLinkedHashSet(Arrays.asList(moduleDependenciesString.split(","))) : Collections.<String>emptySet();

		String lazyModuleDependenciesString = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_LAZY_DEPENDENCIES);
		Set<String> lazyDependentModules = !Strings.isNullOrEmpty(lazyModuleDependenciesString) ? Sets.newLinkedHashSet(Arrays.asList(lazyModuleDependenciesString.split(","))) : Collections.<String>emptySet();

		String externalDependenciesString = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_EXTERNAL_DEPENDENCIES);
		Map<String, String> externalDependencies = BundleUtils.parseExternalDependencies(externalDependenciesString);

		String lazyLoadableString = manifest.get().getMainAttributes().getValue(MANIFEST_ATTR_MODULE_LAZY_LOADABLE);
		Boolean lazyLoadable = lazyLoadableString == null ? false : Boolean.valueOf(lazyLoadableString);

		return new DefaultModuleBundle(source, name, version, format, definitionLang, sourceUrl, dependentModules, lazyDependentModules, externalDependencies, Collections.unmodifiableSet(resourcePaths), lazyLoadable);
	}

	private String getString(String path) throws IOException {
		source.init();
		try {
			if (!source.hasFile(path)) {
				return null;
			}

			final AtomicReference<String> text = new AtomicReference<String>(null);
			source.processFile(path, new FileProcessor() {
				@Override
				public void processFile(String path, IOCallable<? extends InputStream> contents) throws IOException {
					text.set(IOUtils.toString(contents.call(), Charsets.UTF_8));
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

	protected static void extract(final String name, StructuredProcessor source, final StructuredAppender output, final EnumSet<ModuleBundleElement> elements) throws IOException {
		source.processFiles(new FileProcessor() {
			@Override
			public void processFile(String path, IOCallable<? extends InputStream> contents) throws IOException {
				if (path.equals(MANIFEST_MF_PATH)) {
					if (elements.contains(ModuleBundleElement.MANIFEST)) {
						output.subAppender("META-INF").appendFile("MANIFEST.MF", contents.call());
					}
				} else if (path.equals(DEFINITION_PATH)) {
					if (elements.contains(ModuleBundleElement.DEFINITION)) {
						output.appendFile(name + ".def", contents.call());
					}
				} else if (path.equals(JAVASCRIPT_PATH)) {
					if (elements.contains(ModuleBundleElement.JAVASCRIPT)) {
						output.appendFile(name + ".js", contents.call());
					}
				} else if (path.equals(SOURCE_MAP_PATH)) {
					if (elements.contains(ModuleBundleElement.SOURCE_MAP)) {
						output.appendFile(name + ".js.map", contents.call());
					}
				} else {
					if (elements.contains(ModuleBundleElement.RESOURCES) && path.startsWith(RESOURCES_PREFIX)) {
						String resourcePath = path.substring(RESOURCES_PREFIX.length());
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
		for (String supportedVersion : SUPPORTED_VERSIONS_PREFIX) {
			if (spaghettiVersion.startsWith(supportedVersion)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return getName();
	}
}
