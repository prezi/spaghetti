package com.prezi.spaghetti.elm.elm

import java.util.List

class TupleType implements IfaceType {

  private final List<Type> d_types;

  public List<Type> types() {
    return d_types;
  }

  TupleType(List<Type> types) {
    d_types = types;
  }

  @Override
  public Value defaultValue() {
    return new TupleValue(d_types.collect{it.defaultValue()});
  }

  @Override
  public JSType toJSType() {
    return new JSTupleType(this);
  }

  @Override
  public String elmRep() {
    return "(" + d_types.collect{it.elmRep()}.join(", ") + ")";
  }  

  public DictType toDictType() {
    def i = 0;
    return d_types.collectEntries{def tmp = i; i++; return ["a" + tmp, it]};
  }

  @Override
  public String generateSignallingJSFun(String signalName, String elmIface) {
    return ElmUtils.nonUnarySignallingJSFun(signalName, elmIface, this.toDictType());
  }


  @Override
  public String generateCallbackJSFun(String signalName, String elmIface) {
    return ElmUtils.nonUnaryCallbackJSFun(signalName, elmIface, this.toDictType());
  }
  
}