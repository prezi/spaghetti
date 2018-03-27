package com.prezi.spaghetti.typescript.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SpaghettiTypeScriptCommonJsPlugin implements Plugin<Project> {
    private static String COMMON_JS_KEY = "com.prezi.spaghetti.typescript.gradle.SpaghettiTypeScriptCommonJsPlugin";
    private static Object SINGLETON = new Object();

    @Override
    public void apply(Project project) {
        project.getExtensions().add(COMMON_JS_KEY, SINGLETON);
    }

    public static boolean isProjectUsingCommonJs(Project project) {
        return project.getExtensions().findByName(COMMON_JS_KEY) == SINGLETON;
    }
}
