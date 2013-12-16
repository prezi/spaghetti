package com.prezi.spaghetti.elm.js

import com.prezi.spaghetti.elm.elm.Module

import java.util.List


// contains everything that we need to generate a JS iface function
class IfaceFunction implements JSRep {
  
  private final Module d_returnModule;
  private final String d_functionName;
  
  IfaceFunction(Module returnModule, String functionName) {
    d_returnModule = returnModule;
    d_functionName = functionName;
  }

  public Module returnModule() {
    return d_returnModule;
  }

  @Override
  public String jsRep() {
    def jsRep = d_functionName + " : function(div){var __elm = Elm.embed(Elm.";
    jsRep += d_returnModule.moduleHeader().qualifiedModuleName();
    jsRep += ", div);\n"
    jsRep += "return {\n"

    jsRep += d_returnModule.foreignFunctions().collect{
      return "    " + it.generateJS("__elm");
    }.join(",\n");
    jsRep += "\n};\n}"
    return jsRep;
  }


}