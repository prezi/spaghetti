package com.prezi.spaghetti.elm.elm

class IdenType implements Type {

  private final String d_idenStr;

  IdenType (String idenStr) {
    d_idenStr = idenStr;
  }

  @Override
  public String elmRep() {
    return d_idenStr;
  }  
}
