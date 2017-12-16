package com.prezi.spaghetti.typescript.gradle;

import java.util.Set;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import com.google.common.collect.Sets;

public class SpaghettiTypeScriptCommonJsPlugin implements Plugin<Project> {
    private static Set<Project> commonJsProjects = Sets.newHashSet();

    @Override
    public void apply(Project project) {
        commonJsProjects.add(project);
    }

    public static boolean isProjectUsingCommonJs(Project project) {
        return commonJsProjects.contains(project);
    }
}
