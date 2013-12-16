package com.prezi.spaghetti.elm.elm

class ForeignImportDec implements Dec {

  private final String d_exposedName;
  private final Value d_defaultValue;
  private final String d_internalName;
  private final Type d_type;

  ForeignImportDec(String exposedName, String internalName, Type type, Value defaultValue) {
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
