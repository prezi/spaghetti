package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.bundle.ModuleBundle
import com.prezi.spaghetti.bundle.ModuleBundleElement
import com.prezi.spaghetti.bundle.ModuleBundleFactory
import com.prezi.spaghetti.packaging.ModulePackageParameters
import com.prezi.spaghetti.packaging.ModuleType
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class PackageModule extends ConventionTask {

	Object bundle
	void bundle(Object bundle) {
		if (bundle instanceof AbstractBundleModuleTask) {
			dependsOn bundle
		}
		this.bundle = bundle
	}
	@InputDirectory
	File getBundle() {
		if (bundle instanceof AbstractBundleModuleTask) {
			return bundle.getOutputDirectory()
		} else {
			return project.file(bundle)
		}
	}

	@InputFiles
	ConfigurableFileCollection prefixes = project.files()
	void prefixes(Object... prefixes) {
		this.getPrefixes().from(*prefixes)
	}
	void prefix(Object... prefixes) {
		this.prefixes(prefixes)
	}

	@InputFiles
	ConfigurableFileCollection suffixes = project.files()
	void suffixes(Object... suffixes) {
		this.getSuffixes().from(*suffixes)
	}
	void suffix(Object... suffixes) {
		this.suffixes(suffixes)
	}

	@Input
	EnumSet<ModuleBundleElement> elements = ModulePackageParameters.DEFAULT_ELEMENTS
	void elements(Object... elementObjects) {
		this.elements = EnumSet.of(*(elementObjects.collect { Object elemObject ->
			if (elemObject instanceof String) {
				return ModuleBundleElement.valueOf(elemObject)
			} else if (elemObject instanceof ModuleBundleElement) {
				return elemObject
			} else {
				throw new IllegalArgumentException("Unknwon module bundle element: ${elemObject}")
			}
		}))
	}

	@Input
	ModuleType type = ModuleType.COMMON_JS
	void type(String type) {
		switch (type.toUpperCase()) {
			case "AMD":
			case "REQUIREJS":
				this.type = ModuleType.AMD
				break
			case "COMMONJS":
			case "NODE":
			case "NODEJS":
				this.type = ModuleType.COMMON_JS
				break
			default:
				throw new IllegalArgumentException("Unknown module type: ${type}")
		}
	}

	@OutputDirectory
	File outputDirectory
	def outputDirectory(Object outputDirectory) {
		this.outputDirectory = project.file(outputDirectory)
	}

	File getModuleFile() {
		return new File(getOutputDirectory(), getType().packager.getModuleName(loadBundle()))
	}

	PackageModule()
	{
		this.conventionMapping.outputDirectory = { new File(project.buildDir, "spaghetti/module") }
	}

	@TaskAction
	makeBundle() {
		logger.info "Creating {} module in {}", getType().description, getOutputDirectory()
		ModuleBundle bundle = loadBundle()
		getType().packager.packageModuleDirectory(getOutputDirectory(), new ModulePackageParameters(
				bundle: bundle,
				prefixes: getPrefixes().files*.text,
				suffixes: getSuffixes().files*.text
		))
	}

	private ModuleBundle loadBundle() {
		def bundle = ModuleBundleFactory.load(getBundle())
		return bundle
	}
}
