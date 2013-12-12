package com.prezi.spaghetti.elm.elm

class IntType implements DefaultType, Type {

  IntType() {
  }

  @Override
  public Value defaultValue() {
    return new IntValue(0);
  }  

  @Override
  public String elmRep() {
    return "Int";
  }  

  @Override
  public Type toJSType() {
    return new JSNumberType(JSNumberType.INT);
  }
}
