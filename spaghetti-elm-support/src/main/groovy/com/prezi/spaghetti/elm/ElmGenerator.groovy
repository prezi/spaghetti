package com.prezi.spaghetti.elm

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

    elmModules.each{System.out.println(it.elmRep());};
  }
  

  @Override
  void generateApplication(String namespace, File outputDirectory) {
  }


  @Override
  String processModuleJavaScript(ModuleDefinition module, String javaScript) {
  }


  @Override
  String processApplicationJavaScript(String javaScript) {
  }

}

