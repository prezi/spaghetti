package com.prezi.spaghetti.generator;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.prezi.spaghetti.ast.EnumNode;
import com.prezi.spaghetti.ast.EnumValueNode;
import com.prezi.spaghetti.ast.internal.parser.AstParserException;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class EnumGeneratorUtils {

	private enum EnumAssignmentStyle {
		// Enums with no entries
		NoEntries,
		// Enums with one or more entries, all implicit
		Implicit,
		// Enums with one or more entries, all explicit
		Explicit,
		// Enums with at least one implicit and explicit assignment
		Mixed
	}

	public static Map<String, Integer> calculateEnumValues(EnumNode enumNode) {
		Map<String, Integer> enumValues = Maps.newLinkedHashMap();
		switch (detectAssignmentStyle((enumNode))) {
			case NoEntries:
				break;
			case Implicit:
				// Enum entry values are determined from order of appearance
				int ordinal = 0;
				for (EnumValueNode enumValueNode : enumNode.getValues()) {
					enumValues.put(enumValueNode.getName(), ordinal++);
				}
				break;
			case Explicit:
				// Enum entry values are explicitly given
				Set<Integer> seenValues = Sets.newLinkedHashSet();
				for (EnumValueNode enumValueNode : enumNode.getValues()) {
					Integer value = enumValueNode.getValue();
					if (!seenValues.add(value)) {
						throw new AstParserException(
								enumNode.getLocation().getSource(),
								"Duplicate value in enum " + enumNode.getName());
					}
					enumValues.put(enumValueNode.getName(), value);
				}
				break;
			case Mixed:
				throw new AstParserException(
						enumNode.getLocation().getSource(),
						"Mixed implicit and explicit entries in enum " + enumNode.getName());
		}
		return enumValues;
	}

	public static EnumAssignmentStyle detectAssignmentStyle(EnumNode enumNode) {
		EnumAssignmentStyle enumStyle = EnumAssignmentStyle.NoEntries;
		for (EnumValueNode enumValueNode : enumNode.getValues()) {
			EnumAssignmentStyle entryStyle =
					enumValueNode.getValue() == null ? EnumAssignmentStyle.Implicit : EnumAssignmentStyle.Explicit;
			// The first entryStyle determines the enumStyle
			if (enumStyle == EnumAssignmentStyle.NoEntries) {
				enumStyle = entryStyle;
			// If any later entryStyle differs from the enumStyle, we have mixed style
			} else if (enumStyle != entryStyle) {
				return EnumAssignmentStyle.Mixed;
			}
		}
		return enumStyle;
	}
}
