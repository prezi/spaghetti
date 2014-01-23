package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle;
import com.prezi.spaghetti.Wrapper;
import com.prezi.spaghetti.Wrapping;
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction;

import java.util.Set;

class Obfuscate extends AbstractBundleTask {

  private static final List<String> s_protectedSymbols = ["define", "prototype", "__consts"];

  private enum With {
    Closure, UglifyJS
  };

  private With d_with;

  public void withClosure() {
    d_with = With.Closure;
  }

  public void withUglifyJS() {
    d_with = With.UglifyJS;
  }

  private List<String> uglifyJSCommandLine(Set<String> symbols) {

    return ["uglifyjs", inputFile, "--compress", "--mangle",
            "--reserved=" + symbols.join(",")];
  }

  private List<String> closureCommandLine(File closureFile) {

    return ["closure", "--js=" + closureFile, "--compilation_level=ADVANCED_OPTIMIZATIONS"];
  }
  
  public Obfuscate() {
    d_with = With.UglifyJS;
    inputFile = new File(project.buildDir, "spaghetti/module.js");
    outputFile = new File(project.buildDir, "spaghetti/module_obf.zip");
  }

  @TaskAction
  void run() {

    def config = readConfig(definition.text);
    def modules = config.localModules + config.getDependentModules();
    Set<String> symbols = s_protectedSymbols + modules.collect{new SymbolCollectVisitor().visit(it.context)}.flatten();
    def cmdLine = "";

    def cleanup = {};

    switch (d_with) {
    case With.Closure: _: {
      def closureFile = File.createTempFile("closure", "");

      closureFile << inputFile.text << "\nvar __a = {};" + symbols.collect{
        "/** @expose */\n__a." + it + " = {};\n"
      }.join("");

      cmdLine = closureCommandLine(closureFile);
      cleanup = {closureFile.delete()};
      break;
    }
    case With.UglifyJS: _: {
      cmdLine = uglifyJSCommandLine(symbols);
      break;
    }
    default: throw new RuntimeException("Invalid d_with: " + d_with);
    };

    println("Protected API symbols: " + symbols.join(","))
    println("Executing \"" + cmdLine.join(" ") + "\"...");

    def process = cmdLine.execute();
    def compressedJS = new StringBuilder();
    process.waitForProcessOutput(compressedJS, System.err);

    if (process.exitValue() != 0) {
      throw new RuntimeException("Obfuscation failed with exit code " + process.exitValue());
    }
    def bundle = new ModuleBundle(config.localModules.first().name, definition.text, compressedJS.toString());
    bundle.save(outputFile);
    cleanup();
  }

  @Override
  @InputFile
  File getDefinition()
  {
    return super.getDefinition()
  }
}