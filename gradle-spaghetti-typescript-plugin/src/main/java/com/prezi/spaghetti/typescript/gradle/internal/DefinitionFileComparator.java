package com.prezi.spaghetti.typescript.gradle.internal;

import java.io.File;
import com.prezi.typescript.gradle.SerializableFileComparator;

public class DefinitionFileComparator extends SerializableFileComparator {
    private static final long serialVersionUID = 1L;

    public static DefinitionFileComparator INSTANCE = new DefinitionFileComparator();

    private DefinitionFileComparator() {
    }

    /*
     * The .module.ts file should always be the last argument to "tsc"
     * because it references other code in other files and needs to be
     * the at the bottom of the concatenated JavaScript. */
    public int compare(File a, File b) {
        boolean isAModule = a.getPath().endsWith(".module.ts");
        boolean isBModule = b.getPath().endsWith(".module.ts");
        if (isAModule && !isBModule) {
            return 1;
        } else if (isBModule && !isAModule) {
            return -1;
        } else {
            return a.getAbsolutePath().compareTo(b.getAbsolutePath());
        }
    }

    public boolean equals(Object o) {
        if (o instanceof DefinitionFileComparator) {
            return true;
        }
        return super.equals(o);
    }
}
