package com.prezi.spaghetti.cli.commands;

import io.airlift.command.Option;

public abstract class AbstractSpaghettiCommand extends AbstractCommand {
	@Option(name = {"-d", "--direct-dependency-path", "--dependency-path"},
			description = "List of directly dependent module bundles separated by colon (':')")
	protected String directDependencyPath;

	@Option(name = {"-t", "--transitive-dependency-path"},
			description = "List of transitively dependent module bundles separated by colon (':')")
	protected String transitiveDependencyPath;
}
