package com.prezi.spaghetti.elm.elm

class BoolType implements IfaceType {

  BoolType() {
  }

  @Override
  public Value defaultValue() {
    return new BoolValue(false);
  }  

  @Override
  public String elmRep() {
    return "Bool";
  }

  @Override
  public Type toJSType() {
    return new JSBoolType();
  }

  @Override
  public Value fromJSFunction() {
    return new IdenValue("JS.toBool");
  }
}
