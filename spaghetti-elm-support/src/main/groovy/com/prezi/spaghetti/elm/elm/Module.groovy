package com.prezi.spaghetti.elm.elm

import java.util.List

class Module implements ElmRep {

  private final ModuleHeader d_moduleHeader;
  private final List<Import> d_importList;
  private final List<Dec> d_decList;

  Module(ModuleHeader moduleHeader, List<Import> importList, List<Dec> decList) {
    d_moduleHeader = moduleHeader;
    d_importList = importList;
    d_decList = decList;
  }

  @Override
  public String elmRep() {
    def ret = "";
    ret += d_moduleHeader.elmRep();
    ret += "\n";
    for (i in d_importList) {
      ret += i.elmRep();
    }
    ret += "\n";
    for (i in d_decList) {
      ret += i.elmRep();
    }

    return ret;
  }  
}