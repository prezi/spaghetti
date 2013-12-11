package com.prezi.spaghetti.elm.elm

class JSStringType implements DefaultType, Type {

  JSStringType() {
    d_stringType = stringType;
  }

  @Override
  public Value defaultValue() {
    return new AppValue (new IdenValue ("JS.fromString"), new StringType().defaultValue());
    }
  }  

  @Override
  public String elmRep() {
    return "JS.JSString";
  }  
}
package com.prezi.spaghetti.elm.elm

class JSStringType implements DefaultType, Type {

  private final StringType d_stringType;

  JSStringType(StringType stringType) {
    d_stringType = stringType;
  }

  @Override
  public Value defaultValue() {
    return new AppValue (new IdenValue ("JS.fromString"), d_dictType.defaultValue());
  }  

  @Override
  public String elmRep() {
    return "JS.JSString";
  }  
}