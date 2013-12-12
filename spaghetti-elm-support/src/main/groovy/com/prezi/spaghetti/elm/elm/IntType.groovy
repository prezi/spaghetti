package com.prezi.spaghetti.elm.elm

class IntType implements IfaceType {

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

  @Override
  public Value fromJSFunction() {
    return new IdenValue("JS.toInt");
  }
}
