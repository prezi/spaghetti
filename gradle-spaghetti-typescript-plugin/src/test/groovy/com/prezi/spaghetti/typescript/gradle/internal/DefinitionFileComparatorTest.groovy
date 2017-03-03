package com.prezi.spaghetti.typescript.gradle.internal

import spock.lang.Specification

class DefinitionFileComparatorTest extends Specification {
    def "test multiple module files are in the right order"() {
        def files = [
            "C.ts",
            "B.module.ts",
            "B.ts",
            "A.module.ts",
            "A.ts",
        ]
        when:
        def sorted = files.collect({ new File(it) }).toSorted(DefinitionFileComparator.INSTANCE)

        then:
        sorted.collect({ it.name }) == [
            "A.ts",
            "B.ts",
            "C.ts",
            "A.module.ts",
            "B.module.ts",
        ]
    }
}