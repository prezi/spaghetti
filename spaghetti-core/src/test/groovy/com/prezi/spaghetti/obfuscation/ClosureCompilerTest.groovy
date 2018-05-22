package com.prezi.spaghetti.obfuscation

import java.nio.file.Files
import com.prezi.spaghetti.obfuscation.internal.ClosureCompiler
import com.prezi.spaghetti.obfuscation.ClosureTarget
import com.prezi.spaghetti.obfuscation.CompilationLevel
import org.apache.commons.io.FileUtils
import spock.lang.Specification

class ClosureCompilerTest extends Specification {
    def "concat and then obfuscate doesn't rename quoted symbols"() {
        File dir = Files.createTempDirectory("ClosureCompilerTest").toFile();
        dir.mkdirs();
        File entryJs = new File(dir, "Entry.js");
        File moduleJs = new File(dir, "Module.js");
        File externsJs = new File(dir, "Externs.js");
        File outputJs = new File(dir, "output.js")
        File obfuscatedJs = new File(dir, "obfuscated.js");

        when:
        FileUtils.write(externsJs, """
var prezi_module;
""");

        FileUtils.write(entryJs, """
prezi_module=require('./Module.js');
""");

        FileUtils.write(moduleJs, """
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.translations = {
    "fr": [ "one", "two", "three" ],
    "de": [ "one", "two", "three" ],
};
""");

        ClosureCompiler.concat(
            dir,
            new File(outputJs.getName()),
            new File(entryJs.getName()),
            [
                new File(entryJs.getName()),
                new File(moduleJs.getName())
            ],
            [
                new File(externsJs.getName()),
            ],
            ClosureTarget.ES6);

        ClosureCompiler.minify(
            dir,
            outputJs,
            obfuscatedJs,
            new File(dir, "source.map"),
            CompilationLevel.ADVANCED,
            [],
            ClosureTarget.ES6)

        then:
        obfuscatedJs.text == [
            'var a={};',
            'Object.defineProperty(a,"__esModule",{value:!0});',
            'a.a={fr:["one","two","three"],de:["one","two","three"]};',
            'prezi_module=a;\n',
        ].join("")

    }

    def "unknown variable error is ignored"() {
        File dir = Files.createTempDirectory("ClosureCompilerTest").toFile();
        dir.mkdirs();
        File entryJs = new File(dir, "Entry.js");
        File moduleJs = new File(dir, "Module.js");
        File outputJs = new File(dir, "output.js")

        when:
        FileUtils.write(entryJs, """
prezi_module=require('./Module.js');
""");

        FileUtils.write(moduleJs, """
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
unknown.xx();
""");

        def ret = ClosureCompiler.concat(
            dir,
            new File(outputJs.getName()),
            new File(entryJs.getName()),
            [
                new File(entryJs.getName()),
                new File(moduleJs.getName())
            ],
            [],
            ClosureTarget.ES6);

        then:
        ret == 0
        outputJs.text.size() > 10

    }
}