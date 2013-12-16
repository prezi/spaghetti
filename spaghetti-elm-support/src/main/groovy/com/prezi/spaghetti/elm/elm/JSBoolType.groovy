package com.prezi.spaghetti.elm.elm

class JSBoolType implements JSType {

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

  @Override
  public Value fromJSFunction() {
    return new IdenValue("JS.toBool");
  }

  @Override
  public Value toJSFunction() {
    return new IdenValue("JS.fromBool");
  }

}
