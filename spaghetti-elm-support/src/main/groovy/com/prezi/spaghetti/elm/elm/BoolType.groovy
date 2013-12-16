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
  public JSType toJSType() {
    return new JSBoolType();
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
