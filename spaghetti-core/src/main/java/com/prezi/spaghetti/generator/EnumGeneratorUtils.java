package com.prezi.spaghetti.generator;

import com.google.common.collect.Maps;
import com.prezi.spaghetti.ast.EnumNode;
import com.prezi.spaghetti.ast.EnumValueNode;
import com.prezi.spaghetti.ast.internal.parser.AstParserException;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class EnumGeneratorUtils {
	public static Map<String, Integer> calculateEnumValues(EnumNode enumNode) {
		Map<String, Integer> enumValues = Maps.newLinkedHashMap();
		int ordinal = 0;
		boolean seenImplicit = false;
		boolean seenExplicit = false;
		Set<Integer> seenValues = new LinkedHashSet<Integer>();
		for (EnumValueNode enumValueNode : enumNode.getValues()) {
			Integer value = enumValueNode.getValue();
			if (value == null && !seenExplicit) {
				enumValues.put(enumValueNode.getName(), ordinal++);
				seenImplicit = true;
			} else if (value != null && !seenImplicit) {
				if (!seenValues.add(value)) {
					throw new AstParserException(
							enumNode.getLocation().getSource(),
							"Duplicate value in enum " + enumNode.getName());
				}
				enumValues.put(enumValueNode.getName(), value);
			} else {
				throw new AstParserException(
						enumNode.getLocation().getSource(),
						"Mixed implicit and explicit entries in enum " + enumNode.getName());
			}
		}
		return enumValues;
	}
}
