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
  public JSType toJSType() {
    return new JSNumberType(JSNumberType.INT);
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
