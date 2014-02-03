package com.prezi.spaghetti.gradle

public class StackTrace {

	public class LineInfo {
		public int lineNo;
		public String moduleName;
	}

	public List<LineInfo> lines;

	public StackTrace(List<LineInfo> lines) {
		this.lines = lines;
	}

	public static StackTrace parse(String rawStackTrace) {
		return new StackTrace([]);
	}

}