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
        mainFile.text == "prezi_closure_test=require('./folder/folder/module');"
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
            "require('./folder/folder/module')," +
            "require('./module')," +
            "require('../module')];")
    }
}
