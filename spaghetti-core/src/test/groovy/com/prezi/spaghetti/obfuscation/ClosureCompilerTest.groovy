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

    def "circular import is not an error"() {
        File dir = Files.createTempDirectory("ClosureCompilerTest").toFile();
        dir.mkdirs();
        File entryJs = new File(dir, "Entry.js");
        File moduleJs = new File(dir, "Module.js");
        File outputJs = new File(dir, "output.js")

        when:
        FileUtils.write(entryJs, """
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var Module=require('./Module');
console.log(Module.Value);
""");

        FileUtils.write(moduleJs, """
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var Entry=require('./Entry');
exports.Value = "Module value";
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
    }

    def "early reference is an error"() {
        File dir = Files.createTempDirectory("ClosureCompilerTest").toFile();
        dir.mkdirs();
        File entryJs = new File(dir, "Entry.js");
        File moduleJs = new File(dir, "Module.js");
        File outputJs = new File(dir, "output.js")

        when:
        FileUtils.write(entryJs, """
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var Module=require('./Module');
exports.EntryValue = [1,2,3];
""");

        FileUtils.write(moduleJs, """
"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var Entry=require('./Entry');
console.log(Entry.EntryValue);
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
        ret == 1
    }

    def "test single entry point"() {
        when:
        File dir = Files.createTempDirectory("ClosureUtilsTest").toFile();
        dir.mkdirs();

        def mainFile = new File(dir, "main.js");
        ClosureCompiler.writeMainEntryPoint(
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
        ClosureCompiler.writeMainEntryPoint(
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
