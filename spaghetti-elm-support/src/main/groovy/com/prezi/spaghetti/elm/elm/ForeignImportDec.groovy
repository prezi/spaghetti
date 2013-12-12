package com.prezi.spaghetti.elm.elm

class ForeignImportDec implements Dec {

  private String d_exposedName;
  private Value d_defaultValue;
  private String d_internalName;
  private Type d_type;

  ForeignImportDec(String exposedName, Value defaultValue, String internalName, Type type) {
    d_exposedName = exposedName;
    d_defaultValue = defaultValue;
    d_internalName = internalName;
    d_type = type;
  }

  @Override
  public String elmRep() {
    def ret = "";

    ret += "foreign import jsevent \"" + d_exposedName + "\" " + d_defaultValue.elmRep() + "\n";;
    ret += "    " + d_internalName + " : " + d_type.elmRep() + "\n";

    return ret;
  }  
}
