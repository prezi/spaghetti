package com.prezi.spaghetti.ast.internal.parser;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import com.prezi.spaghetti.ast.internal.DefaultLocation;
import com.prezi.spaghetti.ast.internal.DefaultModuleNode;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;


public class SimpleTypeScriptDefinitionParser extends ModuleParser {
    private static final Pattern moduleNamespacePattern =
        Pattern.compile("(?:declare\\s+)?(?:module|namespace)\\s+([a-zA-Z0-9_\\.]+)\\s+\\{");

    public SimpleTypeScriptDefinitionParser(ModuleDefinitionSource source) {
        super(null, createModuleNode(source));
    }

    private static DefaultModuleNode createModuleNode(ModuleDefinitionSource source) {
        DefaultLocation location = new DefaultLocation(source, 0, 0);
        Matcher m = moduleNamespacePattern.matcher(source.getContents());
        boolean found = m.find();
        if (!found || m.groupCount() < 1) {
            throw new AstParserException(source, ": Cannot find module namespace in TypeScript file");
        }
        if (!m.group().startsWith("declare ")) {
            throw new AstParserException(source, ": TypeScript module must be prefixed with 'declare'");
        }
        String namespace = m.group(1);
        String name = namespace.replace(".", "_");
        return new DefaultModuleNode(location, namespace, name);
    }

    public DefaultModuleNode parse(TypeResolver resolver) {
        return this.node;
    }
}