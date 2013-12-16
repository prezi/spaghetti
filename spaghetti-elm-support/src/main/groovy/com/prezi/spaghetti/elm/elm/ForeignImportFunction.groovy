package com.prezi.spaghetti.elm.elm

class ForeignImportFunction implements ForeignFunction {

  IfaceType d_ifaceType;
  String d_methodName;

  ForeignImportFunction(IfaceType ifaceType, String methodName) {
    d_ifaceType = ifaceType;
    d_methodName = methodName;
  }

  @Override
  public Dec foreignDec() {
    return new ForeignImportDec(d_methodName, "js_" + d_methodName,
                                ElmUtils.signal(d_ifaceType.toJSType()),
                                d_ifaceType.toJSType().defaultValue());
  }

  @Override
  public FunDec functionDec() {
    def functionBody =
      new AppValue(
        new AppValue(
          new IdenValue("lift"),
          d_ifaceType.toJSType().fromJSFunction()),
        new IdenValue("js_" + d_methodName));

    return new FunDec(d_methodName, ElmUtils.signal(d_ifaceType), functionBody);
  }
}
