package com.prezi.spaghetti.closure;

import com.google.javascript.jscomp.deps.ModuleLoader;
import com.google.javascript.jscomp.AbstractCommandLineRunner;
import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CompilerOptions.Reach;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.DependencyOptions;
import com.google.javascript.jscomp.DiagnosticGroup;
import com.google.javascript.jscomp.DiagnosticGroups;
import com.google.javascript.jscomp.DiagnosticType;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.ModuleIdentifier;
import com.google.javascript.jscomp.PropertyRenamingPolicy;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.VariableRenamingPolicy;
import com.google.javascript.jscomp.jarjar.com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.parsing.parser.trees.ThisExpressionTree;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.List;


class ClosureWrapper {

    static DiagnosticType EARLY_REFERENCE = findDiagnosticType("JSC_REFERENCE_BEFORE_DECLARE");

    public static void main(String[] args) throws IOException {
        runCompiler(Args.parse(args));
    }

    private static void runCompiler(Args args) throws IOException {

		final CompilationLevel level = CompilationLevel.fromString(args.compilationLevel);
		class Runner extends CommandLineRunner {

			protected Runner(String[] args) {
				super(args);
			}

			@Override
			protected void setRunOptions(CompilerOptions options) throws IOException {
				super.setRunOptions(options);
				if (args.concat) {
					options.setConvertToDottedProperties(false);
					if (level == CompilationLevel.SIMPLE_OPTIMIZATIONS) {
						options.setRenamingPolicy(VariableRenamingPolicy.OFF, PropertyRenamingPolicy.OFF);
						options.setInlineVariables(Reach.NONE);
						options.setInlineFunctions(Reach.NONE);
						options.setFoldConstants(false);
						options.setCoalesceVariableNames(false);
						options.setCollapseVariableDeclarations(false);
					}
				}
				if (args.concat) {
					// Report an error if there is an import cycle in the module resolution.
					// (ie. EARLY_REFERENCE, the module is referenced before it is defined).
					options.setWarningLevel(
						new DiagnosticGroup(EARLY_REFERENCE),
						CheckLevel.WARNING);
					// TODO detect actual static early references
//						CheckLevel.ERROR);
				}
			}

			public Compiler getTheCompiler() {
				return super.getCompiler();
			}
		}

		List<String> argList = new ArrayList<String>();

        if (level == null) {
            System.err.println("Invalid value for compilation_level: " + args.compilationLevel);
            System.exit(2);
        } else if (args.concat) {
            if (level != CompilationLevel.SIMPLE_OPTIMIZATIONS
                    && level != CompilationLevel.ADVANCED_OPTIMIZATIONS) {
                System.err.println("With concat, compilation_level must be SIMPLE or ADVANCED. It was: " + args.compilationLevel);
                System.exit(2);
            }
        }

		argList.add("--compilation_level");
		argList.add(args.compilationLevel);
		argList.add("--env");
		argList.add("BROWSER");

        if (args.concat) {
			argList.add("--process_common_js_modules");
			argList.add("--module_resolution");
			argList.add("NODE");
			argList.add("--dependency_mode");
			argList.add("PRUNE");
			for(String entryPoint : args.entryPoints) {
				argList.add("--entry_point");
				argList.add(entryPoint);
			}
            if (level == CompilationLevel.SIMPLE_OPTIMIZATIONS) {
                // Pretty print output to make local debugging easier.
                // Spaghetti's obfuscation task will obfuscate & minify later in the build process.
				argList.add("--formatting");
				argList.add("PRETTY_PRINT");

            }
        }

        if (args.sourceMap != null) {
			argList.add("--create_source_map");
			argList.add(args.sourceMap.getPath());
        }

		argList.add("--emit_use_strict");

        if (args.es5) {
			argList.add("--language_in");
			argList.add("ECMASCRIPT5_STRICT");
			argList.add("--language_out");
			argList.add("ECMASCRIPT5_STRICT");
        } else {
			argList.add("--language_in");
			argList.add("ECMASCRIPT_2015");
			argList.add("--language_out");
			argList.add("ECMASCRIPT_2015");
			argList.add("--rewrite_polyfills");
			argList.add("false");
        }

		argList.add("--jscomp_off");
		argList.add("checkVars");
		argList.add("--jscomp_off");
		argList.add("checkTypes");
		argList.add("--jscomp_off");
		argList.add("uselessCode");

        for (String path : args.externsPatterns) {
			argList.add("--externs");
			argList.add(path);
        }

        for (String path : args.inputPatterns) {
			argList.add("--js");
			argList.add(path);
        }

		argList.add("--js_output_file");
		argList.add(args.outputFile.getPath());

		Runner runner = new Runner(argList.toArray(new String[0]));
		runner.run();

		Compiler compiler = runner.getTheCompiler();
		ImmutableList<JSError> errors = compiler.getErrors();
        if (errors.size() > 0) {
            for (JSError e : errors) {
                if (args.concat && e.getType() == EARLY_REFERENCE) {
                    System.err.println(String.format("The error '%s'", e.getDescription()));
                    System.err.println("  likely means that there is a cycle in the module imports.");
                    System.err.println("  Please refactor to avoid undefined errors at runtime.");
                    break;
                }
            }
            System.exit(1);
        } else {
            System.out.println("Wrote: " + args.outputFile.getAbsolutePath());

            if (args.sourceMap != null) {
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
}
