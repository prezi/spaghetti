package com.prezi.spaghetti.elm.elm

// type and body belongs together

class FunDec implements Dec {
  private String d_funName;
  private Type d_type;
  private Value d_body;

  FunDec(String funName, Type type, Value body) {
    d_funName = funName;
    d_type = type;
    d_body = body;
  }

  @Override
  public String elmRep() {

    def ret = "";

    ret += d_funName + " : " + d_type.elmRep() + "\n";
    ret += d_funName + " = " + d_body.elmRep() + "\n\n";

    return ret;
  }
  
}
