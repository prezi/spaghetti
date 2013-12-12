package com.prezi.spaghetti.elm.elm

class JSBoolType implements DefaultType, Type {

  JSBoolType() {
  }

  @Override
  public Value defaultValue() {
    return new AppValue (new IdenValue ("JS.fromBool"), new BoolType().defaultValue());
  }  

  @Override
  public String elmRep() {
    return "JS.JSBool";
  }

}
