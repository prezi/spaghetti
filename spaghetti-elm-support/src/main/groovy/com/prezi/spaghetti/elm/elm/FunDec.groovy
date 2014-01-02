package com.prezi.spaghetti.elm.elm

// type and body belong together in Elm

class FunDec implements Dec {

  private final String d_funName;
  private final Type d_type;
  private final Value d_body;

  FunDec(String funName, Type type, Value body) {
    d_funName = funName;
    d_type = type;
    d_body = body;
  }

  public funName() {
    return d_funName;
  }

  @Override
  public String elmRep() {

    def ret = "";

    ret += d_funName + " : " + d_type.elmRep() + "\n";
    ret += d_funName + " = " + d_body.elmRep() + "\n\n";

    return ret;
  }
  
}
