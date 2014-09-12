package com.prezi.spaghetti.cli.commands;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.bundle.ModuleBundle;
import com.prezi.spaghetti.bundle.ModuleBundleFactory;
import io.airlift.command.Option;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public abstract class AbstractSpaghettiCommand extends AbstractCommand {
	@Option(name = {"-d", "--dependency-path"},
			description = "List of directly dependent module bundles separated by colon (':')")
	protected String directDependencyPath;

	protected static Set<ModuleBundle> parseBundles(String path) throws IOException {
		Set<ModuleBundle> bundles = Sets.newLinkedHashSet();
		if (!Strings.isNullOrEmpty(path)) {
			for (String bundlePath : path.split(":")) {
				File bundleFile = new File(bundlePath);
				ModuleBundle bundle = ModuleBundleFactory.load(bundleFile);
				bundles.add(bundle);
			}
		}
		return bundles;
	}
}
