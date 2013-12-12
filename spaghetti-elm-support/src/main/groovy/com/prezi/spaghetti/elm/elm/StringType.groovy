package com.prezi.spaghetti.elm.elm

class StringType implements IfaceType {

  StringType() {
  }

  @Override
  public Value defaultValue() {
    return new StringValue("");
  }  

  @Override
  public String elmRep() {
    return "String";
  }  

  @Override
  public Type toJSType() {
    return new JSStringType();
  }

  @Override
  public Value fromJSFunction() {
    return new IdenValue("JS.toString");
  }
}
