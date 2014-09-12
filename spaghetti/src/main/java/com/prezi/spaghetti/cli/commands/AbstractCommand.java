package com.prezi.spaghetti.cli.commands;

import io.airlift.command.Option;

import java.util.concurrent.Callable;

public abstract class AbstractCommand implements Callable<Integer> {
	@Option(name = {"-v", "--verbose"},
			description = "Verbose mode")
	private boolean verbose;

	@Option(name = {"-q", "--quiet"},
			description = "Quite mode")
	private boolean quiet;

	public boolean isVerbose() {
		return verbose;
	}

	public boolean isQuiet() {
		return quiet;
	}
}
