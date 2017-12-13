package com.prezi.spaghetti.definition.internal;

import java.io.File;

import com.prezi.spaghetti.definition.DefinitionFile;

public class DefaultDefinitionFile implements DefinitionFile {
    private File file;
    private String namespaceOverride;

    public DefaultDefinitionFile(File file, String namespaceOverride) {
        this.file = file;
        this.namespaceOverride = namespaceOverride;
    }

    public File getFile() {
        return file;
    }

    public String getNamespaceOverride() {
        return namespaceOverride;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultDefinitionFile other = (DefaultDefinitionFile) o;
        if (file != null ? !file.equals(other.getFile()) : other.getFile() != null) return false;
        if (namespaceOverride != null ? !namespaceOverride.equals(other.getNamespaceOverride()) : other.getNamespaceOverride() != null) return false;

        return true;
    }
}