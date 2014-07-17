package com.prezi.spaghetti.haxe.gradle;

import com.google.common.io.Files;
import com.prezi.haxe.gradle.MUnit;
import org.apache.commons.io.FileUtils;
import org.gradle.api.Action;
import org.gradle.api.file.CopySpec;
import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class MUnitWithSpaghetti extends MUnit {
	private File testApplication;
	private String testApplicationName;

	@Override
	@Optional
	public File getInputFile() {
		return super.getInputFile();
	}

	@InputDirectory
	public File getTestApplication() {
		return testApplication;
	}

	public void setTestApplication(Object testApplication) {
		this.testApplication = getProject().file(testApplication);
	}

	@SuppressWarnings("UnusedDeclaration")
	public void testApplication(Object testApplication) {
		setTestApplication(testApplication);
	}

	@Input
	public String getTestApplicationName() {
		return testApplicationName;
	}

	public void setTestApplicationName(String testApplicationName) {
		this.testApplicationName = testApplicationName;
	}

	@SuppressWarnings("UnusedDeclaration")
	public void testApplicationName(String testApplicationName) {
		setTestApplicationName(testApplicationName);
	}

	@Override
	protected String copyCompiledTest(final File workDir) {
		try {
			// Extract Require JS
			Properties requireJsProps = new Properties();
			InputStream requireJsPropsStream = getClass().getResourceAsStream("/META-INF/maven/org.webjars/requirejs/pom.properties");
			try {
				requireJsProps.load(requireJsPropsStream);
			} finally {
				requireJsPropsStream.close();
			}

			String requireJsVersion = requireJsProps.getProperty("version");
			File requireJsFile = new File(workDir, "require.js");
			FileUtils.deleteQuietly(requireJsFile);

			InputStream requireJsStream = getClass().getResourceAsStream("/META-INF/resources/webjars/requirejs/" + requireJsVersion + "/require.js");
			try {
				Files.asByteSink(requireJsFile).writeFrom(requireJsStream);
			} finally {
				requireJsStream.close();
			}

			getLogger().debug("Copying test application from {} to {}", getTestApplication(), workDir);
			getServices().get(FileOperations.class).sync(new Action<CopySpec>() {
				@Override
				public void execute(CopySpec copySpec) {
					copySpec.from(getTestApplication());
					copySpec.into(workDir);
				}

			});
			return getTestApplicationName();
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	protected URL getMUnitJsHtmlTemplate() {
		return getClass().getResource("/js_runner-html-with-require.mtt");
	}
}
