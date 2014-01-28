package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.ModuleBundle;
import com.prezi.spaghetti.Wrapper;
import com.prezi.spaghetti.SourceMap;

import java.util.Set;

class ObfuscateBundle extends AbstractBundleTask {

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

  private List<String> uglifyJSCommandLine(Set<String> symbols, File uglifyFile, File mapJStoMinFile) {

    return ["uglifyjs", uglifyFile, "--compress", "--mangle",
            "--reserved=" + symbols.join(","),
            "--source-map=" + mapJStoMinFile,
            "--source-map-url=''"];
  }

  private List<String> closureCommandLine(File closureFile, File mapJStoMinFile) {

    return ["closure", "--js=" + closureFile, "--compilation_level=ADVANCED_OPTIMIZATIONS",
            "--create_source_map=" + mapJStoMinFile];
  }

  public ObfuscateBundle() {
    d_with = With.UglifyJS;
    inputFile = new File(project.buildDir, "spaghetti/module.zip");
    outputFile = new File(project.buildDir, "spaghetti/module_obf.zip");
  }

  @TaskAction
  void run() {

    def config = readConfig(definition.text);
    def modules = config.localModules + config.getDependentModules();
    def cmdLine = "";
    def obfuscateDir = new File(project.buildDir, "obfuscate");
    def mapJStoMinFile = new File(obfuscateDir, "JStoMin.map");
    Set<String> symbols = s_protectedSymbols + modules.collect{new SymbolCollectVisitor().visit(it.context)}.flatten();

    def bundle = ModuleBundle.load(inputFile);

    // OBFUSCATE
    switch (d_with) {
    case With.Closure: _: {
      def closureFile = new File(obfuscateDir, "closure.js");

      closureFile << bundle.bundledJavaScript << "\nvar __a = {};\n" + symbols.collect{
        "/** @expose */\n__a." + it + " = {};\n"
      }.join("");

      cmdLine = closureCommandLine(closureFile, mapJStoMinFile);
      break;
    }
    case With.UglifyJS: _: {
      def closureFile = new File(obfuscateDir, "uglify.js");

      uglifyFile << bundle.bundledJavaScript;
      cmdLine = uglifyJSCommandLine(symbols, uglifyFile, mapJStoMinFile);
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

    // SOURCEMAP
    def mapJStoMin = mapJStoMinFile.text;
    def finalSourceMap;
    if (bundle.sourceMap != null) {
      finalSourceMap = SourceMap.compose(bundle.sourceMap, mapJStoMin, "module.map");
    } else {
      finalSourceMap = mapJStoMin;
    }

    // BUNDLE
    def obfBundle = new ModuleBundle(bundle.name, bundle.definition, compressedJS.toString(), finalSourceMap);
    obfBundle.save(outputFile);
  }

  @Override
  @InputFile
  File getDefinition()
  {
    return super.getDefinition()
  }
}
