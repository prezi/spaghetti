package com.prezi.spaghetti.haxe.gradle.internal;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.gradle.api.Action;
import org.gradle.api.file.CopySpec;
import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CompiledJsCopier {

	private FileOperations operations;
	private Logger logger;

	public CompiledJsCopier(FileOperations operations, Logger logger) {
		this.operations = operations;
		this.logger = logger;
	}

	public String copyCompiledJs(final File workDir, final File testApplication, String testApplicationName) {
		try {
			// Extract Require JS
			Properties requireJsProps = new Properties();
			InputStream requireJsPropsStream = CompiledJsCopier.class.getResourceAsStream("/META-INF/maven/org.webjars/requirejs/pom.properties");
			try {
				requireJsProps.load(requireJsPropsStream);
			} finally {
				requireJsPropsStream.close();
			}

			logger.debug("Copying test application from {} to {}", testApplication, workDir);
			operations.sync(new Action<CopySpec>() {
				@Override
				public void execute(CopySpec copySpec) {
					copySpec.from(testApplication);
					copySpec.into(workDir);
				}
			});

			String requireJsVersion = requireJsProps.getProperty("version");
			File requireJsFile = new File(workDir, "require.js");
			FileUtils.deleteQuietly(requireJsFile);

			InputStream requireJsStream = CompiledJsCopier.class.getResourceAsStream("/META-INF/resources/webjars/requirejs/" + requireJsVersion + "/require.js");
			try {
				Files.asByteSink(requireJsFile).writeFrom(requireJsStream);
			} finally {
				requireJsStream.close();
			}

			return testApplicationName;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
