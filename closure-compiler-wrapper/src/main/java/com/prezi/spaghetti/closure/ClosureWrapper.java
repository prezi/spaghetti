package com.prezi.spaghetti.closure;

import com.google.javascript.jscomp.deps.ModuleLoader;
import com.google.javascript.jscomp.AbstractCommandLineRunner;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.DependencyOptions;
import com.google.javascript.jscomp.DiagnosticGroup;
import com.google.javascript.jscomp.DiagnosticGroups;
import com.google.javascript.jscomp.DiagnosticType;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.ModuleIdentifier;
import com.google.javascript.jscomp.SourceFile;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;


class ClosureWrapper {

    static DiagnosticType EARLY_REFERENCE = findDiagnosticType("JSC_REFERENCE_BEFORE_DECLARE");

    public static void main(String[] args) throws IOException {
        runCompiler(Args.parse(args));
    }

    private static void runCompiler(Args args) throws IOException {

        Compiler compiler = new Compiler(System.err);
        CompilerOptions options = new CompilerOptions();

        if (args.concat) {
            compiler.setErrorManager(new FilteringErrorManager(
                options.errorFormat.toFormatter(compiler, true),
                System.err
            ));
        }

        CompilationLevel level = CompilationLevel.fromString(args.compilationLevel);
        if (level == null) {
            System.err.println("Invalid value for compilation_level: " + args.compilationLevel);
            System.exit(2);
        }

        level.setOptionsForCompilationLevel(options);
        options.setTrustedStrings(true);
        options.setEnvironment(CompilerOptions.Environment.BROWSER);

        if (args.concat) {
            level.setWrappedOutputOptimizations(options);
            options.setProcessCommonJSModules(true);
            options.setConvertToDottedProperties(false);
            options.setModuleResolutionMode(ModuleLoader.ResolutionMode.NODE);
            // Dependency mode STRICT
            options.setDependencyOptions(new DependencyOptions()
                .setDependencyPruning(true)
                .setDependencySorting(true)
                .setMoocherDropping(true)
                .setEntryPoints(getEntryPoints(args.entryPoints)));
        }

        if (args.sourceMap != null) {
            options.setSourceMapOutputPath(args.sourceMap.getPath());
        }

        if (args.es5) {
            options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT);
            options.setLanguageOut(CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT);
        } else {
            options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT_2015);
            options.setLanguageOut(CompilerOptions.LanguageMode.NO_TRANSPILE);
            options.setEmitUseStrict(false);
            options.setRewritePolyfills(false);
        }

        options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.OFF);
        options.setWarningLevel(DiagnosticGroups.CHECK_TYPES, CheckLevel.OFF);

        if (args.concat) {
            // Report an error if there is an import cycle in the module resolution.
            // (ie. EARLY_REFERENCE, the module is referenced before it is defined).
            options.setWarningLevel(
                new DiagnosticGroup(EARLY_REFERENCE),
                CheckLevel.ERROR);
        }

        List<SourceFile> externs = new ArrayList<SourceFile>();
        externs.addAll(AbstractCommandLineRunner.getBuiltinExterns(options.getEnvironment()));
        for (String path : CommandLineRunner.findJsFiles(args.externsPatterns)) {
            externs.add(SourceFile.fromFile(path));
        }

        List<SourceFile> inputs = new ArrayList<SourceFile>();
        for (String path : CommandLineRunner.findJsFiles(args.inputPatterns)) {
            inputs.add(SourceFile.fromFile(path));
        }

        compiler.compile(externs, inputs, options);

        if (compiler.hasErrors()) {
            for (JSError e : compiler.getErrors()) {
                if (args.concat && e.getType() == EARLY_REFERENCE) {
                    System.err.println(String.format("The error '%s'", e.description));
                    System.err.println("  likely means that there is a cycle in the module imports.");
                    System.err.println("  Please refactor to avoid undefined errors at runtime.");
                    break;
                }
            }
            System.exit(1);
        } else {
            Writer writer = new FileWriter(args.outputFile);
            writer.write(compiler.toSource());
            writer.write("\n");
            writer.close();
            System.out.println("Wrote: " + args.outputFile.getAbsolutePath());

            if (args.sourceMap != null) {
                writer = new FileWriter(args.sourceMap);
                compiler.getSourceMap().appendTo(writer, args.outputFile.getPath());
                writer.close();
                System.out.println("Wrote: " + args.sourceMap.getAbsolutePath());
            }
        }
    }

    // VariableReferenceCheck is a protected class, so we have to access
    // VariableReferenceCheck.EARLY_REFERENCE the hacky way.
    static DiagnosticType findDiagnosticType(String key) {
        for (DiagnosticType t : DiagnosticGroups.CHECK_VARIABLES.getTypes()) {
            if (key.equals(t.key)) {
                return t;
            }
        }

        throw new RuntimeException("Cannot locate EARLY_REFERENCE");
    }


    private static List<ModuleIdentifier> getEntryPoints(List<String> entryFiles) {
        List<ModuleIdentifier> entryPoints = new ArrayList<ModuleIdentifier>();
        for (String s : entryFiles) {
            entryPoints.add(ModuleIdentifier.forFile(s));
        }
        return entryPoints;
    }
}
