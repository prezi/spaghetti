package com.prezi.spaghetti.elm.elm

class ForeignExportFunction extends ForeignFunction {

  private final IfaceType d_ifaceType;
  private final String d_methodName;
  private final String d_implModuleAlias;

  ForeignExportFunction(IfaceType ifaceType, String methodName, String implModuleAlias) {
    d_ifaceType = ifaceType;
    d_methodName = methodName;
    d_implModuleAlias = implModuleAlias;
  }

  @Override
  public IfaceType ifaceType() {
    return d_ifaceType;
  }

  private Dec foreignDec() {
    def exportBody =
      new AppValue(
        new AppValue(
          new IdenValue("lift"),
          d_ifaceType.toJSType().toJSFunction()),
        new IdenValue(d_implModuleAlias + "." + d_methodName));
      
    return new ForeignExportDec(d_methodName, "js_" + d_methodName,
                                ElmUtils.signal(d_ifaceType.toJSType()), exportBody);
  }


  @Override
  public String elmRep() {
    return foreignDec().elmRep();
  }

  @Override
  public String methodName() {
    return d_methodName;
  }

  @Override
  public String generateJS(String elmIface) {
    return d_methodName + " : " + d_ifaceType.generateCallbackJSFun(d_methodName, elmIface);
  }

}
