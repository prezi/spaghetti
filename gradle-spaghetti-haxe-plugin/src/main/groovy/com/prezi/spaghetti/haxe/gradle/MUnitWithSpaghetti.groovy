package com.prezi.spaghetti.haxe.gradle

import com.prezi.haxe.gradle.HaxeCommandBuilder
import com.prezi.haxe.gradle.HaxeSourceSet
import com.prezi.haxe.gradle.MUnit
import com.prezi.spaghetti.ModuleBundle
import org.gradle.api.DomainObjectSet
import org.gradle.api.artifacts.Configuration
import org.gradle.language.base.LanguageSourceSet

/**
 * Created by lptr on 27/04/14.
 */
class MUnitWithSpaghetti extends MUnit {

	@Override
	protected HaxeCommandBuilder configureHaxeCommandLine(File output, File workDir, DomainObjectSet<LanguageSourceSet> sources, Set<LanguageSourceSet> testSources, Map<String, File> allResources) {
		def builder = super.configureHaxeCommandLine(output, workDir, sources, testSources, allResources)

		if (getTargetPlatform().name == "js") {
			// Extract Require JS
			def requireJsProps = new Properties()
			requireJsProps.load(this.class.getResourceAsStream("/META-INF/maven/org.webjars/requirejs/pom.properties"))
			def requireJsVersion = requireJsProps.getProperty("version")
			def requireJsFile = new File(workDir, "require.js")
			requireJsFile.delete()
			requireJsFile << this.class.getResourceAsStream("/META-INF/resources/webjars/requirejs/${requireJsVersion}/require.js")

			// Collect all classpath configurations
			Set<Configuration> allClassPaths = []
			[sources, testSources].collectMany(allClassPaths) { DomainObjectSet<LanguageSourceSet> sourceSet ->
				def classPaths = sourceSet.withType(HaxeSourceSet).collect(new LinkedHashSet<Configuration>()) { HaxeSourceSet haxeSourceSet ->
					haxeSourceSet.compileClassPath
				}
				return classPaths
			}
			// Append module locations
			def bundlerCommand = HaxeCommandUtils.spaghettiBundlerCommand("module", output, project.buildDir, allClassPaths, { ModuleBundle bundle ->
				bundle.extract(new File(workDir, bundle.name))
				return bundle.name + "/" + bundle.name
			})
			builder.append("-cmd", "haxe ${bundlerCommand.join(" ")}", )
		}
		return builder
	}

	protected InputStream getMUnitJsHtmlTemplate() {
		return this.class.getResourceAsStream("/js_runner-html-with-require.mtt")
	}
}
