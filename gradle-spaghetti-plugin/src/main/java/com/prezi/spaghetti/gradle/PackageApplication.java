package com.prezi.spaghetti.gradle;

import com.prezi.spaghetti.packaging.ApplicationPackageParameters;
import com.prezi.spaghetti.packaging.ApplicationType;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import static com.prezi.spaghetti.gradle.TextFileUtils.getText;

public class PackageApplication extends AbstractSpaghettiTask {
	private final ConfigurableFileCollection prefixes = getProject().files();
	private final ConfigurableFileCollection suffixes = getProject().files();
	private String mainModule;
	private String baseUrl = ApplicationPackageParameters.DEFAULT_BASE_URL;
	private String applicationName = ApplicationPackageParameters.DEFAULT_APPLICATION_NAME;
	private String modulesDirectory = ApplicationPackageParameters.DEFAULT_MODULES_DIRECTORY;
	private ApplicationType type = ApplicationType.COMMON_JS;
	private Boolean execute = null;
	private File outputDirectory;

	@Input
	@Optional
	public String getMainModule() {
		return mainModule;
	}

	public void setMainModule(String mainModule) {
		this.mainModule = mainModule;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void mainModule(String mainModule) {
		setMainModule(mainModule);
	}

	@Input
	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void baseUrl(String baseUrl) {
		setBaseUrl(baseUrl);
	}

	@Input
	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void applicationName(String applicationName) {
		setApplicationName(applicationName);
	}

	@Input
	public String getModulesDirectory() {
		return modulesDirectory;
	}

	public void setModulesDirectory(String modulesDirectory) {
		this.modulesDirectory = modulesDirectory;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void modulesDirectory(String directory) {
		setModulesDirectory(directory);
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
	public ApplicationType getType() {
		return type;
	}

	public void type(String type) {
		setType(type);
	}

	public void setType(String type) {
		this.type = ApplicationType.fromString(type);
	}

	@Input
	public boolean getExecute() {
		return execute != null ? execute : mainModule != null;
	}

	public void setExecute(boolean execute) {
		this.execute = execute;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void execute(boolean execute) {
		setExecute(execute);
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
	public File getApplicationFile() {
		return new File(getOutputDirectory(), getApplicationName());
	}

	public PackageApplication() {
		this.getConventionMapping().map("outputDirectory", new Callable<File>() {
			@Override
			public File call() throws Exception {
				return new File(getProject().getBuildDir(), "spaghetti/application");
			}

		});
		if ((execute == Boolean.TRUE) && (mainModule == null)) {
			throw new IllegalArgumentException("You need to set mainModule as well when execute is true");
		}
	}

	@TaskAction
	@SuppressWarnings("UnusedDeclaration")
	public void makeBundle() throws IOException {
		ModuleBundleLookupResult bundles = lookupBundles();
		getLogger().info("Creating {} application in {}", getType().getDescription(), getOutputDirectory());
		getType().getPackager().packageApplicationDirectory(getOutputDirectory(), new ApplicationPackageParameters(
				bundles.getAllBundles(),
				getModulesDirectory(),
				getBaseUrl(),
				getApplicationName(),
				getMainModule(),
				getExecute(),
				getText(getPrefixes()),
				getText(getSuffixes())
		));
	}
}
