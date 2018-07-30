package com.prezi.spaghetti.cli.commands;

import com.google.common.collect.Lists;
import com.prezi.spaghetti.bundle.ModuleBundleSet;
import com.prezi.spaghetti.bundle.internal.ModuleBundleLoader;
import io.airlift.command.Option;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class AbstractSpaghettiCommand extends AbstractCommand {
	@Option(name = {"-d", "--dependent-module", "--direct-dependent-module"},
			description = "Specifies a (directly) dependent module bundle file")
	protected List<File> directDependencies = Lists.newArrayList();

	@Option(name = {"-l", "--lazy-dependent-module"},
			description = "Specifies a (lazy) dependent module bundle file")
	protected List<File> lazyDependencies = Lists.newArrayList();

	@Option(name = {"-t", "--transitive-dependent-module"},
			description = "Specifies a (transitively) dependent module bundle file")
	protected List<File> transitiveDependencies = Lists.newArrayList();

	protected ModuleBundleSet lookupBundles() throws IOException {
		return ModuleBundleLoader.loadBundles(directDependencies, lazyDependencies, transitiveDependencies);
	}
}
