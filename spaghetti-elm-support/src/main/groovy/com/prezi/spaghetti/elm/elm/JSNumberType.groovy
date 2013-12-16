package com.prezi.spaghetti.elm.elm

class JSNumberType implements JSType {

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

  @Override
  public Value fromJSFunction() {
    switch (d_numberType) {
    case INT: return new IdenValue("JS.toInt");
    case FLOAT: return new IdenValue("JS.toFloat");
    }
  }

  @Override
  public Value toJSFunction() {
    switch (d_numberType) {
    case INT: return new IdenValue("JS.fromInt");
    case FLOAT: return new IdenValue("JS.fromFloat");
    }
  }
  
}
