package com.prezi.spaghetti.elm.elm

import java.util.List

class ModuleHeader implements ElmRep {

  private String d_moduleName;
  private List<String> d_exports;

  ModuleHeader(String moduleName, List<String> exports) {
    d_moduleName = moduleName;
    d_exports = exports;
  }

  @Override
  public String elmRep() {
    def ret = "";
    
    ret += "module " + d_moduleName + "(";

    for (i in d_exports) {

      ret += d_exports;
      if (i.hasNext()) {
        ret += ", ";
      }
    }

    ret += ") where\n";
  }

}