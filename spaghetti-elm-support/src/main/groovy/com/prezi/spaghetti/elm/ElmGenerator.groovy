package com.prezi.spaghetti.elm

import java.util.List
import com.prezi.spaghetti.elm.js.*

import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.ModuleConfiguration
import com.prezi.spaghetti.ModuleDefinition

class ElmGenerator implements Generator {
  
  private final ModuleConfiguration config

  ElmGenerator(ModuleConfiguration config) {
    this.config = config
  }

  @Override
  void generateModuleHeaders(ModuleDefinition module, File outputDirectory) {
    
    def elmModules = new ElmModuleGenerator(module.context).generateModules();

    // def packageDir = new File(module.name.createNamespacePath(outputDirectory), module.context.name.Name.getText());

    def packageDir = new File(outputDirectory, module.context.name.Name.getText());
    packageDir.mkdirs();

    elmModules.each{
      def file = new File(packageDir, it.moduleHeader().moduleName() + ".elm");
      file.delete();
      file << it.elmRep();
    };
  }
  

  @Override
  void generateApplication(String namespace, File outputDirectory) {
  }


  @Override
  String processModuleJavaScript(ModuleDefinition module, String javaScript) {
    
    def elmModules = new ElmModuleGenerator(module.context).generateModules();

    // def jsGenerator = new JSIfaceGenerator(module.context, elmModules);

    // def ifaceFunctions = jsGenerator.generateIfaceFunctions();

    // def returnStatement = "return {\n" + ifaceFunctions.collect{it.jsRep()}.join(",\n") + "\n};"

    // // System.out.("weel wtf" + returnStatement);
    
    // return javaScript + returnStatement;
    return javaScript;
  }


  @Override
  String processApplicationJavaScript(String javaScript) {
    return javaScript;
  }

}

