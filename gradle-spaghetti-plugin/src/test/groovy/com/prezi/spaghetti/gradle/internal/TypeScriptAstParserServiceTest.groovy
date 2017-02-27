package com.prezi.spaghetti.gradle.internal

import java.nio.file.Files
import spock.lang.Specification
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

class TypeScriptAstParserServiceTest extends Specification {
    def "extract symbols from .d.ts"() {
        File dir = Files.createTempDirectory("TypeScriptAstParserServiceTest").toFile();
        dir.mkdirs();
        Logger logger = Logging.getLogger(TypeScriptAstParserServiceTest.class);
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
        Set<String> symbols = TypeScriptAstParserService.methodCollectExportedSymbols(dir, compilerPath, content, logger);

        then:
        "a,b,c,d,e,f,g,hh,ii,jj,kk,ll" == symbols.toSorted().join(",")
    }
}