package com.prezi.spaghetti.typescript.gradle.internal;

import java.util.List;
import java.util.regex.Pattern;


public class ReferenceDirectiveStripper {
    private static final Pattern referencePathDirectivePattern =
        Pattern.compile("^\\/\\/\\/\\s*<reference\\s+path=");

    public static String stripAndJoin(List<String> input) {
        StringBuilder b = new StringBuilder();
        for (String line: input) {
            if (!isReferencePathDirective(line)) {
                b.append(line).append("\n");
            }
        }
        return b.toString();
    }

    private static boolean isReferencePathDirective(String line) {
        return referencePathDirectivePattern.matcher(line).find();
    }
}