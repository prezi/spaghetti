package com.prezi.spaghetti.haxe.gradle;

import com.prezi.haxe.gradle.MUnitNode;
import com.prezi.spaghetti.haxe.gradle.internal.CompiledJsCopier;
import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Optional;
import org.gradle.process.internal.ExecActionFactory;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;

public class MUnitNodeWithSpaghetti extends MUnitNode implements MUnitTask {

	private File testApplication;
	private String testApplicationName;

	@Inject
	public MUnitNodeWithSpaghetti(ExecActionFactory execActionFactory) {
		super(execActionFactory);
	}

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
		return new CompiledJsCopier(getServices().get(FileOperations.class), getLogger())
				.copyCompiledJs(workDir, getTestApplication(), getTestApplicationName());
	}

	@Override
	protected URL getMUnitJsHtmlTemplate() {
		return getClass().getResource("/js_runner-html-with-require.mtt");
	}
}
