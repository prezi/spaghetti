package com.prezi.spaghetti.elm.elm

import java.util.List

class Module implements ElmRep {

  private final ModuleHeader d_moduleHeader;
  private final List<Import> d_importList;
  private final List<ForeignFunction> d_foreignFunctions;
  private final List<Dec> d_decList;
  private final boolean d_mayOverwrite; // the generated file may be overwritten (no user content)

  Module(ModuleHeader moduleHeader, List<Import> importList, List<ForeignFunction> foreignFunctions, List<Dec> decList, boolean mayOverwrite) {
    d_moduleHeader = moduleHeader;
    d_importList = importList;
    d_foreignFunctions = foreignFunctions;
    d_decList = decList;
    d_mayOverwrite = mayOverwrite
  }

  public ModuleHeader moduleHeader() {
    return d_moduleHeader;
  }

  public List<ForeignFunction> foreignFunctions() {
    return d_foreignFunctions;
  }

  public boolean mayOverwrite() {
    return d_mayOverwrite;
  }

  @Override
  public String elmRep() {
    def ret = d_moduleHeader.elmRep() + "\n" +
      d_importList.collect{it.elmRep()}.join("") + "\n" +
      d_foreignFunctions.collect{it.elmRep()}.join("") +
      d_decList.collect{it.elmRep()}.join("");

    return ret;
  }  
}