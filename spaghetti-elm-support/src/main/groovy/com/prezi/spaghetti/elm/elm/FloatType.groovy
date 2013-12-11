package com.prezi.spaghetti.elm.elm

class FloatType implements DefaultType, Type {

  FloatType() {
  }

  @Override
  public Value defaultValue() {
    return new FloatValue(0.0);
  }  

  @Override
  public String elmRep() {
    return "Float";
  }  

  @Override
  public Type toJSType() {
    return new JSNumber(JSNumberType.FLOAT);
  }
}
