package com.prezi.spaghetti.elm.elm

class ForeignExportDec implements Dec {

  private final String d_exposedName;
  private final String d_internalName;
  private final Value d_body;
  private final Type d_type;

  ForeignExportDec(String exposedName, String internalName, Type type, Value body) {
    d_exposedName = exposedName;
    d_internalName = internalName;
    d_body = body;
    d_type = type;
  }

  @Override
  public String elmRep() {
    def ret = "foreign export jsevent \"" + d_exposedName + "\"\n" +
              "    " + d_internalName + " : " + d_type.elmRep() + "\n" +
              d_internalName + " = " + d_body.elmRep() + "\n";

    return ret;
  }  
}
