package com.prezi.spaghetti.elm.elm

class BoolType implements DefaultType, Type, ToJSType {

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
    return new JSBool(this);
  }

}
