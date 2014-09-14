package com.prezi.spaghetti.gradle.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackTrace {
	private static final List<LineMatcher> LINE_MATCHERS = Arrays.asList((LineMatcher) new DefaultLimeMatcher());
	private final List<StackTraceLine> lines;

	public StackTrace(List<StackTraceLine> lines) {
		this.lines = lines;
	}

	public List<StackTraceLine> getLines() {
		return lines;
	}

	public static StackTrace parse(String rawStackTrace) throws IOException {
		ImmutableList.Builder<StackTraceLine> stackTraceLines = ImmutableList.builder();
		for (String line : CharStreams.readLines(new StringReader(rawStackTrace))) {
			for (LineMatcher lineMatcher : LINE_MATCHERS) {
				StackTraceLine match = lineMatcher.matchLine(line);
				if (match != null) {
					stackTraceLines.add(match);
				}
			}
		}

		return new StackTrace(stackTraceLines.build());
	}

	private interface LineMatcher {
		StackTraceLine matchLine(String line);
	}

	private static class DefaultLimeMatcher implements LineMatcher {
		private static final Pattern PATTERN = Pattern.compile("([a-zA-Z0-9._-]*)\\.js:(\\d+)");

		@Override
		public StackTraceLine matchLine(String line) {
			Matcher matcher = PATTERN.matcher(line);
			if (matcher.find()) {
				String jsName = matcher.group(1);
				Integer lineNumber = Integer.decode(matcher.group(2));
				return new StackTraceLine(lineNumber, jsName);
			}
			return null;
		}
	}

	public static class StackTraceLine {
		public final int lineNo;
		public final String jsName;

		public StackTraceLine(int lineNo, String jsName) {
			this.lineNo = lineNo;
			this.jsName = jsName;
		}
	}
}
