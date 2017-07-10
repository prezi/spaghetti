package com.prezi.spaghetti.typescript.gradle.internal

import spock.lang.Specification

class ReferenceDirectiveStripperTest extends Specification {
    def "test strips reference path"() {
        def lines = [
            '/// <reference path="A.ts" />',
            '/// <reference types="node" />',
            '/// <reference path="../../X.module.ts" />',
            "module B {",
            "    export function b() { }",
            "}",
            "",
        ]
        when:
        def output = ReferenceDirectiveStripper.stripAndJoin(lines);

        then:
        output.split('\n') == [
            '/// <reference types="node" />',
            "module B {",
            "    export function b() { }",
            "}",
        ]
    }
}