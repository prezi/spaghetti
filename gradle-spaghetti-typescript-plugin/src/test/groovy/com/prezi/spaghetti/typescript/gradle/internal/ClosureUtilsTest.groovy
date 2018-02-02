package com.prezi.spaghetti.typescript.gradle.internal

import java.nio.file.Files
import spock.lang.Specification

class ClosureUtilsTest extends Specification {
    def "test single entry point"() {
        when:
        File dir = Files.createTempDirectory("ClosureUtilsTest").toFile();
        dir.mkdirs();

        def mainFile = new File(dir, "main.js");
        ClosureUtils.writeMainEntryPoint(
            mainFile,
            [ new File(dir, "folder/folder/module.js") ],
            "prezi_closure_test");

        then:
        def absPath = dir.getAbsolutePath();
        mainFile.text == "prezi_closure_test=require('${absPath}/folder/folder/module.js');"
    }

    def "test multiple entry points"() {
        when:
        File dir = Files.createTempDirectory("ClosureUtilsTest").toFile();
        dir.mkdirs();

        def mainFile = new File(dir, "main.js");
        ClosureUtils.writeMainEntryPoint(
            mainFile,
            [
                new File(dir, "folder/folder/module.js"),
                new File(dir, "module.js"),
                new File(dir.getParentFile(), "module.js"),
            ],
            "prezi_closure_test");

        then:
        mainFile.text == ("prezi_closure_test=[" +
            "require('${dir.getAbsolutePath()}/folder/folder/module.js')," +
            "require('${dir.getAbsolutePath()}/module.js')," +
            "require('${dir.getParent()}/module.js')];")
    }
}