package com.prezi.spaghetti.elm.elm

class StringType implements DefaultType, Type {

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
}
