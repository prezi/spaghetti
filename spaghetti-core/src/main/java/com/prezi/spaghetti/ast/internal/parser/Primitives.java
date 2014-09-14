package com.prezi.spaghetti.ast.internal.parser;

import org.antlr.v4.runtime.Token;
import org.apache.commons.lang.StringEscapeUtils;

public class Primitives {
	public static Integer parseInt(Token token) {
		String text = (token == null ? null : token.getText());
		if (text != null) {
			int sign = 1;
			if (text.startsWith("-")) {
				sign = -1;
				text = text.substring(1);
			} else if (text.startsWith("+")) {
				text = text.substring(1);
			}

			int value;
			if (text.startsWith("0x") || text.startsWith("0X")) {
				value = Integer.parseInt(text.substring(2), 16);
			} else {
				value = Integer.parseInt(text);
			}

			return sign * value;
		} else {
			return null;
		}
	}

	public static Double parseDouble(Token token) {
		String text = (token == null ? null : token.getText());
		if (text != null) {
			return Double.parseDouble(text);
		} else {
			return null;
		}
	}

	public static Boolean parseBoolean(Token token) {
		String text = (token == null ? null : token.getText());
		if (text != null) {
			return text.equals("true");
		} else {
			return null;
		}
	}

	public static String parseString(Token token) {
		String text = (token == null ? null : token.getText());
		if (text != null) {
			String unescaped = StringEscapeUtils.unescapeJava(text);
			// strip surrounding quotes
			return unescaped.substring(1, unescaped.length() - 1);
		} else {
			return null;
		}

	}
}
