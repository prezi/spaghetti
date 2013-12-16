package com.prezi.spaghetti.elm.elm

interface IfaceType extends DefaultType {
  JSType toJSType();
  String generateSignallingJSFun(String signalName, String elmIface);
  String generateCallbackJSFun(String signalName, String elmIface);
}
