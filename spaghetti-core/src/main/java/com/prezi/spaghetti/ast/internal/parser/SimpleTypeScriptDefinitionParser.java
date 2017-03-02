package com.prezi.spaghetti.ast.internal.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.prezi.spaghetti.ast.internal.DefaultLocation;
import com.prezi.spaghetti.ast.internal.DefaultModuleNode;
import com.prezi.spaghetti.definition.ModuleDefinitionSource;
import com.prezi.spaghetti.definition.internal.DefaultModuleDefinitionSource;


public class SimpleTypeScriptDefinitionParser extends ModuleParser {
    public static final String DEFERRED_DTS_CONTENTS = "<< Definition contents will be generated later; if you see this string in a file it is likely a Spaghetti bug >>";

    private static final Pattern moduleNamespacePattern =
        Pattern.compile("(?:declare\\s+)?(?:module|namespace)\\s+([a-zA-Z0-9_\\.]+)\\s+\\{");

    public SimpleTypeScriptDefinitionParser(ModuleDefinitionSource source) {
        super(null, createModuleNode(source));
    }

    private static DefaultModuleNode createModuleNode(ModuleDefinitionSource source) {
        Matcher m = moduleNamespacePattern.matcher(source.getContents());
        boolean found = m.find();
        if (!found || m.groupCount() < 1) {
            throw new AstParserException(source, ": Cannot find module namespace in TypeScript file");
        }
        String namespace = m.group(1);
        String name = namespace.replace(".", "_");

        if (source.getLocation().endsWith(".ts") && !source.getLocation().endsWith(".d.ts")) {
            ModuleDefinitionSource deferredSource = DefaultModuleDefinitionSource.fromStringWithLang(
                source.getLocation(),
                DEFERRED_DTS_CONTENTS,
                source.getDefinitionLanguage());
            DefaultLocation location = new DefaultLocation(deferredSource, 0, 0);
            return new DefaultModuleNode(location, namespace, name);
        } else {
            if (!m.group().startsWith("declare ")) {
                throw new AstParserException(source, ": TypeScript definition must be prefixed with 'declare'");
            }

            DefaultLocation location = new DefaultLocation(source, 0, 0);
            return new DefaultModuleNode(location, namespace, name);
        }
    }

    public DefaultModuleNode parse(TypeResolver resolver) {
        return this.node;
    }
}