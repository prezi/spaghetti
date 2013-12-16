package com.prezi.spaghetti.elm.elm

class ForeignExportFunction implements ForeignFunction {

  private final IfaceType d_ifaceType;
  private final String d_methodName;

  ForeignExportFunction(IfaceType ifaceType, String methodName) {
    d_ifaceType = ifaceType;
    d_methodName = methodName;
  }

  @Override
  public Dec foreignDec() {
    def exportBody =
      new AppValue(
        new AppValue(
          new IdenValue("lift"),
          d_ifaceType.toJSType().toJSFunction()),
        new IdenValue(d_methodName));
      
    return new ForeignExportDec(d_methodName, "js_" + d_methodName,
                                ElmUtils.signal(d_ifaceType.toJSType()), exportBody);
  }

  @Override
  public FunDec functionDec() {
    def functionBody = new AppValue(new IdenValue("constant"), d_ifaceType.defaultValue());
    return new FunDec(d_methodName, ElmUtils.signal(d_ifaceType), functionBody);
  }

  @Override
  public String generateJS(String elmIface) {
    return d_methodName + " : " + d_ifaceType.generateCallbackJSFun(d_methodName, elmIface);
  }

}
