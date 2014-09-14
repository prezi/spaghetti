package com.prezi.spaghetti.packaging.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentUtils {
	public static class SplitResult {
		public final String initialComments;
		public final String actualCode;

		public SplitResult(String initialComments, String actualCode) {
			this.initialComments = initialComments;
			this.actualCode = actualCode;
		}
	}

	public static void appendAfterInitialComment(StringBuilder builder, String prefix, String javaScript) {
		SplitResult result = splitInitialComments(javaScript);
		builder.append(result.initialComments).append(prefix).append(result.actualCode);
	}

	public static SplitResult splitInitialComments(String javaScript) {
		Pattern regex = Pattern.compile("(?sx)^"		// s: '.' matches newline, x: allow comments
						+"("
							+ "(?:"						// match any number of comments
								+ "\\s*"				// separated by any whitespace
								+ "(?:"
									+ "//.*?"			// match full line comment
										+ "(?:\\n|$)"	// ending with a newline or at end of file
									+ "|"
									+ "/\\*.*?\\*/"		// match block comment
								+ ")"
							+ ")*"
							+ "\\s*"					// match any remaining whitespace after comments
						+ ")"
						+ "(.*)"						// the actual code
					+ "$");
		Matcher matcher = regex.matcher(javaScript);
		if (!matcher.matches()) {
			throw new AssertionError("JavaScript code did not match prefix comment finder regex: \"${javaScript}\"");
		}
		String comments = matcher.group(1);
		String code = matcher.group(2);
		return new SplitResult(comments, code);
	}
}
