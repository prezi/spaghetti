package com.prezi.spaghetti.packaging

class CommentUtils {
	static class SplitResult {
		String initialComments
		String actualCode
	}

	static void appendAfterInitialComment(StringBuilder builder, String prefix, String javaScript) {
		def result = splitInitialComments(javaScript)
		builder.append(result.initialComments).append(prefix).append(result.actualCode)
	}

	static SplitResult splitInitialComments(String javaScript) {
		def regex = /(?sx)^								# s: '.' matches newline, x: allow comments
						(
							(?:							# match any number of comments
								\s*						# 	separated by any whitespace
								(?:
									\/\/.*?				# match full line comment
										(?:\n|$)		# ending with a newline or at end of file
									|
									\/\*.*?\*\/			# match block comment
								)
							)*
							\s*							# match any remaining whitespace after comments
						)
						(.*)							# the actual code
					$/
		def matcher = javaScript =~ regex;
		if (!matcher.matches()) {
			throw new AssertionError("JavaScript code did not match prefix comment finder regex: \"${javaScript}\"")
		}
		def comments = matcher.group(1)
		def code = matcher.group(2)
		return new SplitResult(initialComments: comments, actualCode: code)
	}
}
