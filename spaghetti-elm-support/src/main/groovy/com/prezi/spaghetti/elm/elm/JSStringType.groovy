package com.prezi.spaghetti.elm.elm

class JSStringType implements JSType {

  JSStringType() {
  }

  @Override
  public Value defaultValue() {
    return new AppValue (new IdenValue ("JS.fromString"), new StringType().defaultValue());
  }  

  @Override
  public String elmRep() {
    return "JS.JSString";
  }  

  @Override
  public Value fromJSFunction() {
    return new IdenValue("JS.toString");
  }

  @Override
  public Value toJSFunction() {
    return new IdenValue("JS.fromString");
  }
}
