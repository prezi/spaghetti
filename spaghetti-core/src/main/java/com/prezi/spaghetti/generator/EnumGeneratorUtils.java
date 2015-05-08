package com.prezi.spaghetti.generator;

import com.google.common.collect.Maps;
import com.prezi.spaghetti.ast.EnumNode;
import com.prezi.spaghetti.ast.EnumValueNode;
import com.prezi.spaghetti.ast.internal.parser.AstParserException;

import java.util.Map;

public class EnumGeneratorUtils {
	public static Map<String, Integer> calculateEnumValues(EnumNode enumNode) {
		Map<String, Integer> enumValues = Maps.newLinkedHashMap();
		int currentValue = 0;
		for (EnumValueNode enumValueNode : enumNode.getValues()) {
			Integer value = enumValueNode.getValue();
			if (value == null) {
				value = currentValue;
			} else {
				if (value < currentValue) {
					// TODO Have a nicer error message
					throw new AstParserException(enumNode.getLocation().getSource(), "Enum value is wrong");
				}
				currentValue = value;
			}
			enumValues.put(enumValueNode.getName(), value);
			currentValue++;
		}
		return enumValues;
	}
}
