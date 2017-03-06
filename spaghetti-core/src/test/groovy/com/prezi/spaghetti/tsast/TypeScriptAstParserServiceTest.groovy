package com.prezi.spaghetti.tsast

import java.nio.file.Files
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spock.lang.Specification

class TypeScriptAstParserServiceTest extends Specification {
    def "extract symbols from .d.ts"() {
        File dir = Files.createTempDirectory("TypeScriptAstParserServiceTest").toFile();
        dir.mkdirs();
        Logger logger = LoggerFactory.getLogger(TypeScriptAstParserServiceTest.class)
        File compilerPath = new File("build/typescript/node_modules/typescript");

        when:
        def content = """
declare module a.b.c {
    enum d {
        e,
        f,
        g
    }

    interface XInterface {
        hh(xparam: string): void;
    }

    type XType = number | string;

    const ii:XInterface;
    function jj(xparam: number, xparam: string): { kk: number, ll: string };
}
        """
        Set<String> symbols = TypeScriptAstParserService.collectExportedSymbols(dir, compilerPath, content, logger);

        then:
        def l = symbols.toList()
        l.sort()
        l.join(",") == "a,b,c,d,e,f,g,hh,ii,jj,kk,ll"
    }

    def "classes not allowed"() {
        when:
        def lines = runVerify("""
module test {
    export class Test {
    }
}
""")
        then:
        def e = thrown(TypeScriptAstParserException)
        e.output[0].contains("Classes are not allowed.")
    }

    def "var, let not allowed"() {
        when:
        def lines = runVerify("""
module test {
    export var test: number;
    export let test2: number;
}
""")
        then:
        def e = thrown(TypeScriptAstParserException)
        e.output[0].contains("'var' and 'let' are not allowed")
    }

    def "references not allowed"() {
        when:
        def lines = runVerify("""
/// <reference path="internal/other.ts"/>
""")
        then:
        def e = thrown(TypeScriptAstParserException)
        e.output[0].contains("Reference directives are not allowed")
    }

    def "multiple top-level statements not allowed"() {
        when:
        def lines = runVerify("""
module test {}
interface A {}
""")
        then:
        def e = thrown(TypeScriptAstParserException)
        e.output[0].contains("Expecting only one module declaration")
    }

    def "untyped variables are not allowed"() {
        when:
        def lines = runVerify("""
module test {
    export const a = "a";
    export const b;
    export const c: any;
}
""")
        then:
        def e = thrown(TypeScriptAstParserException)
        e.output[0].contains("Variables without explicit types are not allowed")
        e.output[1].contains("Variables without explicit types are not allowed")
        e.output[2].contains("Variables should not have 'any' type")
    }

    def "non-exported are ignored in non-ambient context"() {
        when:
        def lines = runVerify("""
module test {
    let a = "a";
    var b;
    class A {}
}
""")
        then:
        lines == []
    }

    def "ambient context members are implicitly exported"() {
        when:
        def lines = runVerify("""
declare module test {
    class A {}
}
""")
        then:
        def e = thrown(TypeScriptAstParserException)
        e.output[0].contains("Classes are not allowed.")
    }

    def "ambient non-exported sub-context members are ignored"() {
        when:
        def lines = runVerify("""
module test {
    var a: any;

    declare module B {
        class C {}
    }
}
""")
        then:
        lines == []
    }

    def "ambient sub-context members are implicitly exported"() {
        when:
        def lines = runVerify("""
module test {
    var a: any;

    export module B {
        export declare module C {
            class D {}
        }
    }
}
""")
        then:
        def e = thrown(TypeScriptAstParserException)
        e.output[0].contains("Classes are not allowed.")
    }


    def "non-exported sub-module members are ignored"() {
        when:
        def lines = runVerify("""
module test {
    var a: any;

    export module B {
        module C {
            class D {}
        }
    }
}
""")
        then:
        lines == []
    }

    def "var, let inside functions bodies are ignored"() {
        when:
        def lines = runVerify("""
module test {
    export function a(): number {
        var b = 1;
        let c = 2;
        return b + c;
    }
}
""")
        then:
        lines == []
    }

    def runVerify(String content) {
        File dir = Files.createTempDirectory("TypeScriptAstParserServiceTest").toFile();
        dir.mkdirs();

        File definitionFile = new File(dir, "definition.d.ts");
        FileUtils.write(definitionFile, content);

        Logger logger = LoggerFactory.getLogger(TypeScriptAstParserServiceTest.class);
        File compilerPath = new File("build/typescript/node_modules/typescript");

        return TypeScriptAstParserService.verifyModuleDefinition(dir, compilerPath, definitionFile, logger);
    }
}