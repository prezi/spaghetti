package com.prezi.spaghetti.elm.elm

class StringType implements IfaceType {

  StringType() {
  }

  @Override
  public Value defaultValue() {
    return new StringValue("");
  }  

  @Override
  public String elmRep() {
    return "String";
  }  

  @Override
  public JSType toJSType() {
    return new JSStringType();
  }

  @Override
  public String generateSignallingJSFun(String signalName, String elmIface) {
    return ElmUtils.unarySignallingJSFun(signalName, elmIface);
  }
}
