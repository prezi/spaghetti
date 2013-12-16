package com.prezi.spaghetti.elm.elm

public class ElmUtils {

  private static String unarySignallingJSFun(String signalName, String elmIface) {
    return "function(a){" + elmIface + ".send('" + signalName + "', a);}";
  }

  private static String nonUnarySignallingJSFun(String signalName, String elmIface, DictType dictType) {
    def argList = dictType.map().keySet().join(", ");
    def argsJSON = "{" + dictType.map().keySet().collect{return it + " : " + it;}.join(", ");
    return "function(" + argList + "){" + elmIface + ".send('" + signalName + "', " + argsJSON + "});}";
  }

  public static Type signal(Type t) {
    new AppType(new IdenType("Signal"), [t]);
  }

}
