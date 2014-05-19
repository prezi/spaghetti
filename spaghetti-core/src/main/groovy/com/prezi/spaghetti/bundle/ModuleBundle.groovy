package com.prezi.spaghetti.bundle

import com.prezi.spaghetti.bundle.BundleBuilder.BundleAppender
import com.prezi.spaghetti.definition.ModuleType

/**
 * Created by lptr on 16/11/13.
 */
interface ModuleBundle extends Comparable<ModuleBundle> {
	String getName()
	ModuleType getType()
	String getVersion()
	String getSourceBaseUrl()
	Set<String> getDependentModules()
	Set<String> getResourcePaths()

	String getDefinition()
	String getJavaScript()
	String getSourceMap()

	void extract(BundleAppender output, EnumSet<ModuleBundleElement> elements)
}
