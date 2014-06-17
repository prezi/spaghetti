package com.prezi.spaghetti.bundle;

import com.prezi.spaghetti.structure.StructuredAppender;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

public interface ModuleBundle extends Comparable<ModuleBundle> {
	public static final String DEFINITION_PATH = "module.def";
	public static final String SOURCE_MAP_PATH = "module.map";
	public static final String JAVASCRIPT_PATH = "module.js";
	public static final String MANIFEST_MF_PATH = "META-INF/MANIFEST.MF";
	public static final String RESOURCES_PREFIX = "resources/";

	String getName();
	String getVersion();
	String getSourceBaseUrl();
	Set<String> getDependentModules();
	Set<String> getResourcePaths();
	String getDefinition() throws IOException;
	String getJavaScript() throws IOException;
	String getSourceMap() throws IOException;
	void extract(StructuredAppender output, EnumSet<ModuleBundleElement> elements) throws IOException;
}
