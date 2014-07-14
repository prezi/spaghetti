package com.prezi.spaghetti.haxe.gradle

import com.prezi.haxe.gradle.MUnit
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional

class MUnitWithSpaghetti extends MUnit {

	@Override
	@Optional
	File getInputFile() {
		return super.getInputFile()
	}

	@InputDirectory
	File testApplication

	@Input
	String testApplicationName

	@Override
	protected String copyCompiledTest(File workDir) {
		// Extract Require JS
		def requireJsProps = new Properties()
		requireJsProps.load(this.class.getResourceAsStream("/META-INF/maven/org.webjars/requirejs/pom.properties"))
		def requireJsVersion = requireJsProps.getProperty("version")
		def requireJsFile = new File(workDir, "require.js")
		requireJsFile.delete()
		requireJsFile << this.class.getResourceAsStream("/META-INF/resources/webjars/requirejs/${requireJsVersion}/require.js")

		logger.debug "Copying test application from {} to {}", getTestApplication(), workDir
		project.copy {
			from getTestApplication()
			into workDir
		}
		return getTestApplicationName()
	}

	@Override
	protected URL getMUnitJsHtmlTemplate() {
		return this.class.getResource("/js_runner-html-with-require.mtt")
	}
}
