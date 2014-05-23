package com.prezi.spaghetti.bundle

import com.prezi.spaghetti.structure.StructuredWriter
import com.prezi.spaghetti.definition.ModuleType

/**
 * Created by lptr on 16/11/13.
 */
interface ModuleBundle extends Comparable<ModuleBundle> {
	static final def DEFINITION_PATH = "module.def"
	static final def SOURCE_MAP_PATH = "module.map"
	static final def JAVASCRIPT_PATH = "module.js"
	static final def MANIFEST_MF_PATH = "META-INF/MANIFEST.MF"
	static final def RESOURCES_PREFIX = "resources/"

	String getName()
	ModuleType getType()
	String getVersion()
	String getSourceBaseUrl()
	Set<String> getDependentModules()
	Set<String> getResourcePaths()

	String getDefinition()
	String getJavaScript()
	String getSourceMap()

	void extract(StructuredWriter.StructuredAppender output, EnumSet<ModuleBundleElement> elements)
}
