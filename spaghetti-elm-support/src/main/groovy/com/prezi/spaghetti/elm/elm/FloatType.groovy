package com.prezi.spaghetti.elm.elm

class FloatType implements IfaceType {

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
    return new JSNumberType(JSNumberType.FLOAT);
  }

  @Override
  public Value fromJSFunction() {
    return new IdenValue("JS.toFloat");
  }
}
