package com.prezi.spaghetti.elm.elm

class JSObjectType implements JSType {

  // The underlying dictionary
  private final DictType d_dictType;

  DictType getDictType() {
    return d_dictType;
  }

  JSObjectType(DictType dictType) {
    d_dictType = dictType;
  }

  @Override
  public Value defaultValue() {
    return new AppValue (new IdenValue ("JS.fromRecord"), d_dictType.defaultValue());
  }  

  @Override
  public String elmRep() {
    return "JS.JSObject";
  }  

  @Override
  public Value fromJSFunction() {
    return new IdenValue("JS.toRecord");
  }

  @Override
  public Value toJSFunction() {
    return new IdenValue("JS.fromRecord");
  }

}
