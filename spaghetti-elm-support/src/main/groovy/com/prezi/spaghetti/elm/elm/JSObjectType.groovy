package com.prezi.spaghetti.elm.elm

class JSObjectType implements DefaultType, Type {

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
}
