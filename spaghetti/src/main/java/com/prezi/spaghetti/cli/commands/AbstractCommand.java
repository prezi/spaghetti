package com.prezi.spaghetti.cli.commands;

import io.airlift.command.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

public abstract class AbstractCommand implements Callable<Integer> {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractCommand.class);

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
