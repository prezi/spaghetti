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
  public JSType toJSType() {
    return new JSNumberType(JSNumberType.FLOAT);
  }

  @Override
  public String generateSignallingJSFun(String signalName, String elmIface) {
    return ElmUtils.unarySignallingJSFun(signalName, elmIface);
  }


  @Override
  public String generateCallbackJSFun(String signalName, String elmIface) {
    return ElmUtils.unaryCallbackJSFun(signalName, elmIface);
  }
}
