package com.prezi.spaghetti.elm.elm

class JSStringType implements DefaultType, Type {

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
}
