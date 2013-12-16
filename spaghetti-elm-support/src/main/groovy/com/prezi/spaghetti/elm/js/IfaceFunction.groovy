package com.prezi.spaghetti.elm.js

import java.util.List


// contains everything that we need to generate a JS iface function
class IfaceFunction implements JSRep {
  
  private final JSElmModule d_returnModule;
  private final String d_functionName;
  
  IfaceFunction(JSElmModule returnModule, String functionName) {
    d_returnModule = returnModule;
    d_functionName = functionName;
  }

  public JSElmModule returnModule() {
    return d_returnModule;
  }

  @Override
  public String jsRep() {
    def jsRep = d_functionName + " : function(div){var __elm = Elm.embed(Elm.";
    jsRep += d_returnModule.module().moduleHeader().qualifiedModuleName();
    jsRep += ", div);\n"
    jsRep += "return {\n"

    def typeMethodPairs = [d_returnModule.ifaceTypes(), d_returnModule.methodNames()].transpose();

    jsRep += typeMethodPairs.collect{
      return "    " + it[1] + " : " + it[0].generateSignallingJSFun(it[1],"__elm");
    }.join(",\n");
    jsRep += "\n};\n}"
    return jsRep;
  }


}