package com.prezi.spaghetti.haxe.gradle

import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.gradle.ModuleDefinitionLookup
import org.gradle.api.artifacts.Configuration

/**
 * Created by lptr on 27/04/14.
 */
class HaxeCommandUtils {
	public static List<String> spaghettiBundlerCommand(String spaghettiType, File output, File buildDir, Collection<Configuration> configurations, Closure<String> moduleNamer)
	{
		def workDir = new File(buildDir, "spaghetti-haxe")
		workDir.delete() || workDir.deleteDir()
		workDir.mkdirs()

		def bundleFile = new File(workDir, "SpaghettiBundler.hx")
		bundleFile.delete()
		bundleFile << HaxeCommandUtils.class.classLoader.getResourceAsStream("SpaghettiBundler.hx").text

		List<String> result = ["-cp", bundleFile.parentFile.absolutePath, "--run", "SpaghettiBundler", spaghettiType, output.absolutePath]
		def bundles = configurations.collectMany(new HashSet<ModuleBundle>()) { configuration ->
			ModuleDefinitionLookup.getAllBundles((Configuration) configuration)
		}
		result.addAll(bundles.collect { ModuleBundle bundle -> moduleNamer(bundle) }.sort { it }.unique())
		return result
	}
}
