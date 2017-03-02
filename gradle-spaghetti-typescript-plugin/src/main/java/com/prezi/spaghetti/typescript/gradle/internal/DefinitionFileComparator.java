package com.prezi.spaghetti.typescript.gradle.internal;

import java.io.File;
import com.prezi.typescript.gradle.SerializableFileComparator;

public class DefinitionFileComparator extends SerializableFileComparator {
    private static final long serialVersionUID = 1L;

    public static DefinitionFileComparator INSTANCE = new DefinitionFileComparator();

    private DefinitionFileComparator() {
    }

    public int compare(File a, File b) {
        if (a.getPath().endsWith(".module.ts")) {
            return 1;
        } else if (b.getPath().endsWith(".module.ts")) {
            return -1;
        }
        return 0;
    }

    public boolean equals(Object o) {
        if (o instanceof DefinitionFileComparator) {
            return true;
        }
        return super.equals(o);
    }
}
