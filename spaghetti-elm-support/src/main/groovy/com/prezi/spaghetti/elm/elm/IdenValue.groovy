package com.prezi.spaghetti.elm.elm

class IdenValue implements Value {

  private final String d_idenStr;

  IdenValue (String idenStr) {
    d_idenStr = idenStr;
  }

  @Override
  public String elmRep() {
    return d_idenStr;
  }  
}
