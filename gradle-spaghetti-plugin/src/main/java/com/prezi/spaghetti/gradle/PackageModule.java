package com.prezi.spaghetti.gradle;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.internal.ModuleBundleElement;
import com.prezi.spaghetti.bundle.internal.ModuleBundleFactory;
import com.prezi.spaghetti.gradle.internal.AbstractBundleModuleTask;
import com.prezi.spaghetti.packaging.ModulePackageParameters;
import com.prezi.spaghetti.packaging.ModuleType;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.concurrent.Callable;

import static com.prezi.spaghetti.gradle.internal.TextFileUtils.getText;

@SuppressWarnings("UnusedDeclaration")
public class PackageModule extends ConventionTask {
	private final ConfigurableFileCollection prefixes = getProject().files();
	private final ConfigurableFileCollection suffixes = getProject().files();
	private Object bundle;
	private EnumSet<ModuleBundleElement> elements = ModulePackageParameters.DEFAULT_ELEMENTS;
	private ModuleType type = ModuleType.COMMON_JS;
	private File outputDirectory;

	public void setBundle(Object bundle) {
		if (bundle instanceof AbstractBundleModuleTask) {
			dependsOn(bundle);
		}
		this.bundle = bundle;
	}

	public void bundle(Object bundle) {
		setBundle(bundle);
	}

	@InputDirectory
	public File getBundle() {
		if (bundle instanceof AbstractBundleModuleTask) {
			return ((AbstractBundleModuleTask) bundle).getOutputDirectory();
		} else {
			return getProject().file(bundle);
		}
	}

	@InputFiles
	public ConfigurableFileCollection getPrefixes() {
		return prefixes;
	}

	public void prefixes(Object... prefixes) {
		this.getPrefixes().from(prefixes);
	}

	public void prefix(Object... prefixes) {
		this.prefixes(prefixes);
	}

	@InputFiles
	public ConfigurableFileCollection getSuffixes() {
		return suffixes;
	}

	public void suffixes(Object... suffixes) {
		this.getSuffixes().from(suffixes);
	}

	public void suffix(Object... suffixes) {
		this.suffixes(suffixes);
	}

	@Input
	@SuppressWarnings("UnusedDeclaration")
	public EnumSet<ModuleBundleElement> getElements() {
		return elements;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void elements(Object... elementObjects) {
		Iterable<ModuleBundleElement> elements = Iterables.transform(Arrays.asList(elementObjects), new Function<Object, ModuleBundleElement>() {
			@Override
			public ModuleBundleElement apply(final Object elemObject) {
				if (elemObject instanceof String) {
					return ModuleBundleElement.valueOf((String) elemObject);
				} else if (elemObject instanceof ModuleBundleElement) {
					return (ModuleBundleElement) elemObject;
				} else {
					throw new IllegalArgumentException("Unknwon module bundle element: " + String.valueOf(elemObject));
				}
			}
		});
		this.elements = Sets.newEnumSet(elements, ModuleBundleElement.class);
	}

	@Input
	public ModuleType getType() {
		return type;
	}

	public void type(String type) {
		setType(type);
	}

	public void setType(String typeName) {
		this.type = ModuleType.fromString(typeName);
	}

	@OutputDirectory
	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(Object outputDirectory) {
		this.outputDirectory = getProject().file(outputDirectory);
	}

	public void outputDirectory(Object outputDirectory) {
		setOutputDirectory(outputDirectory);
	}

	@SuppressWarnings("UnusedDeclaration")
	public File getModuleFile() throws IOException {
		ModuleBundle bundle = ModuleBundleFactory.load(getBundle());
		return new File(getOutputDirectory(), getType().getPackager().getModuleName(bundle));
	}

	@SuppressWarnings("UnusedDeclaration")
	public PackageModule() {
		this.getConventionMapping().map("outputDirectory", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return new File(getProject().getBuildDir(), "spaghetti/module");
			}
		});
	}

	@TaskAction
	@SuppressWarnings("UnusedDeclaration")
	public void makeBundle() throws IOException {
		getLogger().info("Creating {} module in {}", getType().getDescription(), getOutputDirectory());
		ModuleBundle bundle = ModuleBundleFactory.load(getBundle());

		ModulePackageParameters params = new ModulePackageParameters(bundle, getText(getPrefixes()), getText(getSuffixes()));
		getType().getPackager().packageModuleDirectory(getOutputDirectory(), params);
	}
}
