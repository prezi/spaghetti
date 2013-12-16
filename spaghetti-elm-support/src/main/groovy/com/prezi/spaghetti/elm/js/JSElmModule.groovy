package com.prezi.spaghetti.elm.js

import com.prezi.spaghetti.elm.elm.*

import java.util.List

// a JSElmModule is an Elm Module definition together with the interface types and method names
class JSElmModule {
  
  private final Module d_module;
  private final List<IfaceType> d_ifaceTypes;
  private final List<String> d_methodNames;

  JSElmModule(Module module, List<IfaceType> ifaceTypes, List<String> methodNames) {
    d_module = module;
    d_ifaceTypes = ifaceTypes;
    d_methodNames = methodNames;
  }

  public Module module() {
    return d_module;
  }

  public List<IfaceType> ifaceTypes() {
    return d_ifaceTypes;
  }

  public List<String> methodNames() {
    return d_methodNames;
  }

}