package com.prezi.spaghetti.cli;

import ch.qos.logback.classic.Level;
import com.prezi.spaghetti.cli.commands.AbstractCommand;
import com.prezi.spaghetti.cli.commands.BundleModuleCommand;
import com.prezi.spaghetti.cli.commands.GenerateHeadersCommand;
import io.airlift.command.Cli;
import io.airlift.command.Help;
import io.airlift.command.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class SpaghettiCli {
	private static final Logger logger = LoggerFactory.getLogger(SpaghettiCli.class);

	@SuppressWarnings("unchecked")
	public static void main(String... arguments) {
		List<String> args = Arrays.asList(arguments);
		Cli.CliBuilder<Callable<?>> builder = Cli.builder("spaghetti");
		builder
				.withDescription("typed JavaScript module system")
				.withDefaultCommand(Help.class)
				.withCommands(
						GenerateHeadersCommand.class,
						BundleModuleCommand.class,
						Help.class
				);

		Cli<Callable<?>> parser = builder.build();
		int exitValue;
		try {
			Callable<?> callable = null;
			try {
				callable = parser.parse(args);
			} catch (ParseException e) {
				if (args.contains("-v") || args.contains("--verbose")) {
					throw e;
				}

				logger.error("{}", e.getMessage());
				System.exit(-1);
			}

			boolean verbose = false;
			try {
				if (callable instanceof AbstractCommand) {
					AbstractCommand command = (AbstractCommand) callable;
					ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
					if (command.isVerbose()) {
						rootLogger.setLevel(Level.DEBUG);
						verbose = true;
					} else if (command.isQuiet()) {
						rootLogger.setLevel(Level.WARN);
					}
				}
				Object result = callable.call();
				if (result instanceof Integer) {
					exitValue = (Integer) result;
				} else {
					exitValue = 0;
				}
			} catch (SpaghettiCliException e) {
				if (verbose) {
					throw e;
				}

				logExceptions(e);
				exitValue = -1;
			}
		} catch (Exception e) {
			logger.error("Exception:", e);
			exitValue = -1;
		}
		System.exit(exitValue);
	}

	private static void logExceptions(Throwable t) {
		if (t != null) {
			logExceptions(t.getCause());
			if (t instanceof SpaghettiCliException) {
				logger.error("{}", t.getMessage());
			}
		}
	}
}
