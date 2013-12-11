package com.prezi.spaghetti.elm.elm

class StringType implements DefaultType, Type {

  @Override
  public Value defaultValue() {
    return new StringValue("");
  }  

  @Override
  public List<String> elmRep() {
    def ret = new List<String>();
    ret.add("String");
    return ret;
  }  
}
