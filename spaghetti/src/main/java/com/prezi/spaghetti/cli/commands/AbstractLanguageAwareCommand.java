package com.prezi.spaghetti.cli.commands;

import com.google.common.collect.ImmutableMap;
import io.airlift.command.Option;

import java.util.List;
import java.util.Map;

public abstract class AbstractLanguageAwareCommand extends AbstractDefinitionAwareCommand {

	@Option(name = {"-l", "--language"},
			description = "Implementation language",
			required = true)
	protected String language;

	@Option(name = {"-D", "--define"},
			description = "Define an option for the generator")
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private List<String> options;

	protected Map<String, String> getParsedOptions() {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		if (options != null) {
			for (String option : options) {
				String[] parts = option.split("=", 2);
				String value;
				if (parts.length == 1) {
					value = "true";
				} else {
					value = parts[1];
				}
				builder.put(parts[0], value);
			}
		}
		return builder.build();
	}
}
