package com.prezi.spaghetti.elm.elm

import java.util.List

class ModuleHeader implements ElmRep {

  private String d_moduleName, d_packageName;
  private List<String> d_exports;

  ModuleHeader(String packageName, String moduleName, List<String> exports) {
    d_moduleName = moduleName;
    d_packageName = packageName;
    d_exports = exports;
  }

  public String moduleName() {
    return d_moduleName;
  }

  public String qualifiedModuleName() {
    return d_packageName + "." + d_moduleName;
  }

  @Override
  public String elmRep() {
    def ret = "";
    
    ret += "module " + qualifiedModuleName();

    if (d_exports.size() > 0) {
      ret += "(" + d_exports.join(", ") + ")";
    }
    ret += " where\n";
  }
  
}