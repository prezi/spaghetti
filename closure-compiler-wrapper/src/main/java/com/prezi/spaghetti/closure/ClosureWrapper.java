package com.prezi.spaghetti.closure;

import com.google.javascript.jscomp.deps.ModuleLoader;
import com.google.javascript.jscomp.AbstractCommandLineRunner;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.DependencyOptions;
import com.google.javascript.jscomp.DiagnosticGroups;
import com.google.javascript.jscomp.ModuleIdentifier;
import com.google.javascript.jscomp.SourceFile;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

class Args {
    @Option(name="--js_output_file")
    public File outputFile;

    @Option(name="--entry_point")
    public List<String> entryPoints = new ArrayList<String>();

    @Option(name="--js")
    public List<File> inputSources = new ArrayList<File>();

    @Option(name="--externs")
    public List<File> externsSources = new ArrayList<File>();

    @Option(name="--target")
    public String target = "none";
}

class ClosureWrapper {

    private static List<ModuleIdentifier> getEntryPoints(List<String> entryFiles) {
        List<ModuleIdentifier> entryPoints = new ArrayList<ModuleIdentifier>();
        for (String s : entryFiles) {
            entryPoints.add(ModuleIdentifier.forFile(s));
        }
        return entryPoints;
    }

    public static void main(String[] args) throws IOException {
        Args parsedArgs = new Args();
        CmdLineParser parser = new CmdLineParser(parsedArgs);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            return;
        }

        Compiler compiler = new Compiler(System.err);
        CompilerOptions options = new CompilerOptions();

        CompilationLevel level = CompilationLevel.WHITESPACE_ONLY;
        level.setOptionsForCompilationLevel(options);
        level.setWrappedOutputOptimizations(options);
        options.setProcessCommonJSModules(true);
        options.setTrustedStrings(true);
        options.setModuleResolutionMode(ModuleLoader.ResolutionMode.NODE);
        // Dependency mode STRICT
        options.setDependencyOptions(new DependencyOptions()
            .setDependencyPruning(true)
            .setDependencySorting(true)
            .setMoocherDropping(true)
            .setEntryPoints(getEntryPoints(parsedArgs.entryPoints)));

        if (parsedArgs.target.toUpperCase().equals("ES5")) {
            options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT);
            options.setLanguageOut(CompilerOptions.LanguageMode.ECMASCRIPT5_STRICT);
        } else {
            options.setLanguageIn(CompilerOptions.LanguageMode.ECMASCRIPT_2015);
            options.setLanguageOut(CompilerOptions.LanguageMode.ECMASCRIPT_2015);
        }

        options.setWarningLevel(DiagnosticGroups.CHECK_VARIABLES, CheckLevel.ERROR);

        List<SourceFile> externs = new ArrayList<SourceFile>();
        for (File f : parsedArgs.externsSources) {
            externs.add(SourceFile.fromFile(f));
        }

        List<SourceFile> inputs = new ArrayList<SourceFile>();
        for (File f : parsedArgs.inputSources) {
            inputs.add(SourceFile.fromFile(f));
        }

        compiler.compile(externs, inputs, options);
        if (compiler.hasErrors()) {
            System.exit(1);
        } else {
            Writer writer = new FileWriter(parsedArgs.outputFile);
            writer.write(compiler.toSource());
            writer.write("\n");
            writer.close();
            System.out.println("Wrote: " + parsedArgs.outputFile.getAbsolutePath());
        }
    }
}