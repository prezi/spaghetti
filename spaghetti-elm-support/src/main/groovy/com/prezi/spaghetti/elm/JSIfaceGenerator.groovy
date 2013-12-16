package com.prezi.spaghetti.elm

import com.prezi.spaghetti.elm.elm.*
import com.prezi.spaghetti.elm.js.*
import com.prezi.spaghetti.grammar.ModuleParser

class JSIfaceGenerator {

  private final ModuleParser.ModuleDefinitionContext d_moduleDef;
  // elm module name->module map
  private final Map<String, JSElmModule> d_moduleMap;
  
  JSIfaceGenerator(ModuleParser.ModuleDefinitionContext moduleDef, List<JSElmModule> elmModules) {
    d_moduleDef = moduleDef;
    d_moduleMap = elmModules.collectEntries{[it.module().moduleHeader().moduleName(), it]};
  }

  public List<IfaceFunction> generateIfaceFunctions() {
    def methodDefs = d_moduleDef.moduleElement().collect{it.methodDefinition()} - null;

    return methodDefs.collect{generateIfaceFunction(it)};
  }

  public IfaceFunction generateIfaceFunction(ModuleParser.MethodDefinitionContext methodDef) {

    def functionName = methodDef.Name().getText();

    // TODO error handling, check for nulls, each means to a malformed .module
    // retrieve elm module
    def normalReturnType = (ModuleParser.NormalReturnTypeChainContext) methodDef.returnTypeChain();
    def normalValueType = (ModuleParser.NormalValueTypeChainContext) normalReturnType.typeChain();
    def moduleType = normalValueType.valueType().moduleType();
    
    def elmModuleName = moduleType.name.Name.getText();

    def elmModule = d_moduleMap.get(elmModuleName);

    if (!elmModule) {
      throw new ElmException("Type '" + elmModuleName + "' not found in .module");
    }

    return new IfaceFunction(elmModule, functionName);
  }

}
