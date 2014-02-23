package com.prezi.spaghetti.gradle

import java.util.regex.Pattern;

public class StackTrace {

	private static class LineMatcher {
		Pattern pattern
		// index of line number group
		int lineIx
		// index of JS source group
		int jsIx
	}

	private static final List<LineMatcher> LINE_MATCHERS =
		[
			// chrome copy-paste stack,
			// chrome console stack,
			// ff console stack
			new LineMatcher(pattern: ~/([a-zA-Z0-9._-]*)\.js:(\d+)/, lineIx: 2, jsIx: 1)
		];

	public static class LineInfo {
		int lineNo;
		String jsName;
	}

	public final List<LineInfo> lines;

	public StackTrace(List<LineInfo> lines) {
		this.lines = lines;
	}

	public static StackTrace parse(String rawStackTrace) {
		def lines = rawStackTrace.readLines().collect{
			for (lineMatcher in LINE_MATCHERS) {
				def matcher = lineMatcher.pattern.matcher(it);
				if (matcher.size() > 0) {
					return new LineInfo(
							lineNo: Integer.decode(matcher[0][lineMatcher.lineIx]),
							jsName: matcher[0][lineMatcher.jsIx]
					)
				}
			}
			return null;
		};

		return new StackTrace(lines);
	}

}
