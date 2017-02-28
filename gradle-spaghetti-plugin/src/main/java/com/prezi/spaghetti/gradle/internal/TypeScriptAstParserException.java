package com.prezi.spaghetti.gradle.internal;

import java.util.List;

public class TypeScriptAstParserException extends RuntimeException {
    private List<String> outputLines;

    public TypeScriptAstParserException(String message, List<String> outputLines) {
        super(message);
        this.outputLines = outputLines;
    }

    public List<String> getOutput() {
        return outputLines;
    }
}