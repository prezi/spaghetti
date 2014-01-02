package com.prezi.spaghetti.elm.elm

public class ElmUtils {

  private static String unarySignallingJSFun(String signalName, String elmIface) {
    return "function(a){" + elmIface + ".send('" + signalName + "', a);}";
  }

  private static String unaryCallbackJSFun(String signalName, String elmIface) {
    return "function(f){" + elmIface + ".recv('" + signalName + "', function(e){" +
      "f(e.value);});}";
  }

  private static String nonUnarySignallingJSFun(String signalName, String elmIface, DictType dictType) {
    def argList = dictType.map().keySet().join(", ");
    def argsJSON = "{" + dictType.map().keySet().collect{return it + " : " + it;}.join(", ");
    return "function(" + argList + "){" + elmIface + ".send('" + signalName + "', " + argsJSON + "});}";
  }

  private static String nonUnaryCallbackJSFun(String signalName, String elmIface, DictType dictType) {
    return "function(f){" + elmIface + ".recv('" + signalName + "', function(e){" +
      "f(" + dictType.map().keySet().collect{"e.value." + it}.join(", ") + ");});}";
    
  }

  public static Type signal(Type t) {
    return new AppType(new IdenType("Signal"), [t]);
  }

  public static Value defaultSignalValue(IfaceType t) {
    return new AppValue(new IdenValue("constant"), t.defaultValue());
  }

}
