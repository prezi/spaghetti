package com.prezi.spaghetti.gradle;

import java.io.File;
import org.gradle.api.Task;

public interface NeedsTypeScriptCompilerSpaghettiTask extends Task {
    void setCompilerPath(File compilerDir);
}