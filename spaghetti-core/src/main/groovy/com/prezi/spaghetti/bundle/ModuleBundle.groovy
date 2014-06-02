package com.prezi.spaghetti.bundle

import com.prezi.spaghetti.structure.StructuredWriter

/**
 * Created by lptr on 16/11/13.
 */
interface ModuleBundle extends Comparable<ModuleBundle> {
	public static final def DEFINITION_PATH = "module.def"
	public static final def SOURCE_MAP_PATH = "module.map"
	public static final def JAVASCRIPT_PATH = "module.js"
	public static final def MANIFEST_MF_PATH = "META-INF/MANIFEST.MF"
	public static final def RESOURCES_PREFIX = "resources/"

	String getName()
	String getVersion()
	String getSourceBaseUrl()
	Set<String> getDependentModules()
	Set<String> getResourcePaths()

	String getDefinition()
	String getJavaScript()
	String getSourceMap()

	void extract(StructuredWriter.StructuredAppender output, EnumSet<ModuleBundleElement> elements)
}
