package com.prezi.spaghetti.gradle

import java.util.regex.*;

public class StackTrace {

	private class LineMatcher {
		public Pattern pattern;
		public int lineIx, jsIx; // index of regex groups
	}

	private static final List<LineMatcher> lmatchers =
		[
			// chrome copy-paste stack,
			// chrome console stack,
			// ff console stack
			[pattern : ~/([a-zA-Z0-9._-]*)\.js:(\d+)/,
			 lineIx : 2,
			 jsIx : 1]
		];

	public class LineInfo {
		public int lineNo;
		public String jsName;

		public LineInfo(int lineNo, String jsName) {
			this.lineNo = lineNo;
			this.jsName = jsName;
		}
	}

	public List<LineInfo> lines;

	public StackTrace(List<LineInfo> lines) {
		this.lines = lines;
	}

	public static StackTrace parse(String rawStackTrace) {

		def lines = rawStackTrace.readLines().collect{
			for (l in lmatchers) {
				def m = l.pattern.matcher(it);
				if (m.size() > 0) {
					return [lineNo : Integer.decode(m[0][l.lineIx]),
							jsName : m[0][l.jsIx]];
				}
			}
			return null;
		};

		return new StackTrace(lines);
	}

}
