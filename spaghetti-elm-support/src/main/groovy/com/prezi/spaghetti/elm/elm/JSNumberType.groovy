package com.prezi.spaghetti.elm.elm

class JSNumberType implements DefaultType, Type {

  public enum NumberType {
    INT, FLOAT                 // Float not used for now
  }

  private final NumberType d_numberType

  JSNumberType(NumberType numberType) {
    d_numberType = numberType;
  }

  @Override
  public Value defaultValue() {
    switch (d_numberType) {
    case NumberType.INT: return new AppValue (new IdenValue ("JS.fromInt"), new IntType().defaultValue());
    case NumberType.FLOAT: return new AppValue (new IdenValue ("JS.fromFloat"), new FloatType().defaultValue());
    }
  }  

  @Override
  public String elmRep() {
    return "JS.JSNumber";
  }  
}
