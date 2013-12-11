package com.prezi.spaghetti.elm.elm

class AppValue implements Value {

  private final Value d_fun, d_arg;

  AppValue(Value fun, Value arg) {
    d_fun = fun;
    d_arg = arg;
  }

  @Override
  public String elmRep() {
    return "(" + d_fun.elmRep() + " " + d_arg.elmRep() + ")";
  }  
}
