package com.prezi.spaghetti.elm.elm

class ForeignImportFunction extends ForeignFunction {

  IfaceType d_ifaceType;
  String d_methodName;

  ForeignImportFunction(IfaceType ifaceType, String methodName) {
    d_ifaceType = ifaceType;
    d_methodName = methodName;
  }

  public Dec foreignDec() {
    return new ForeignImportDec(d_methodName, "js_" + d_methodName,
                                ElmUtils.signal(d_ifaceType.toJSType()),
                                d_ifaceType.toJSType().defaultValue());
  }

  @Override
  public String elmRep() {
    return foreignDec().elmRep() + functionDec().elmRep() + "\n";
  }

  @Override
  public IfaceType ifaceType() {
    return d_ifaceType;
  }

  @Override
  public String methodName() {
    return d_methodName;
  }

  public FunDec functionDec() {
    def functionBody =
      new AppValue(
        new AppValue(
          new IdenValue("lift"),
          d_ifaceType.toJSType().fromJSFunction()),
        new IdenValue("js_" + d_methodName));

    return new FunDec(d_methodName, ElmUtils.signal(d_ifaceType), functionBody);
  }

  @Override
  public String generateJS(String elmIface) {
    return d_methodName + " : " + d_ifaceType.generateSignallingJSFun(d_methodName, elmIface);
  }

}
