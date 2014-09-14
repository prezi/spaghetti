package com.prezi.spaghetti.structure;

import com.google.common.base.Strings;

import java.io.File;

/**
 * Type of the structured output: ZIP file or exploded directory.
 */
public enum OutputType {
	/**
	 * Output the structure into an exploded directory.
	 */
	DIRECTORY,

	/**
	 * Output the structure into a ZIP file.
	 */
	ZIP;

	public static OutputType fromString(String type, File output) {
		if (Strings.isNullOrEmpty(type)) {
			if (output.getName().endsWith(".zip")) {
				return OutputType.ZIP;
			} else {
				return OutputType.DIRECTORY;
			}
		} else if (type.toUpperCase().equals("ZIP")) {
			return OutputType.ZIP;
		} else if (type.toUpperCase().equals("DIR") || type.toUpperCase().equals("DIRECTORY")){
			return OutputType.DIRECTORY;
		} else {
			throw new IllegalArgumentException("Invalid bundle type: " + type);
		}
	}
}
