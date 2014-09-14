package com.prezi.spaghetti.cli.commands;

import com.prezi.spaghetti.structure.OutputType;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.packaging.ApplicationPackageParameters;
import com.prezi.spaghetti.packaging.ApplicationType;
import io.airlift.command.Command;
import io.airlift.command.Option;

import java.io.File;
import java.util.Collections;
import java.util.Set;

@Command(name = "package", description = "Package an application")
public class PackageApplicationCommand extends AbstractSpaghettiCommand {
	@Option(name = {"-T", "--type"},
			description = "Output type: zip or directory")
	private String type;

	@Option(name = {"--wrapper"},
			description = "Type of wrapper (AMD/RequireJS, NodeJS/CommonJS)",
			required = true)
	private String wrapper;

	@Option(name = {"-o", "--output"},
			description = "Output directory of ZIP file",
			required = true)
	private File output;

	@Option(name = {"--name"},
			description = "Application name (defaults to 'application')")
	private String name;

	@Option(name = {"--main"},
			description = "Main module")
	private String mainModule;

	@Option(name = {"--execute"},
			description = "Whether or not to auto-execute main()")
	private Boolean execute;

	@Override
	public Integer call() throws Exception {
		OutputType type = OutputType.fromString(this.type, output);
		ApplicationType wrapper = ApplicationType.fromString(this.wrapper);

		Set<ModuleBundle> bundles = parseBundles(directDependencyPath);

		ApplicationPackageParameters params = new ApplicationPackageParameters(
				bundles,
				name != null ? name + ".js" : "application.js",
				mainModule,
				execute != null ? execute : mainModule != null,
				Collections.<String> emptySet(),
				Collections.<String> emptySet()
		);

		switch (type) {
			case DIRECTORY:
				wrapper.getPackager().packageApplicationDirectory(output, params);
				break;
			case ZIP:
				wrapper.getPackager().packageApplicationZip(output, params);
				break;
		}
		return 0;
	}
}
