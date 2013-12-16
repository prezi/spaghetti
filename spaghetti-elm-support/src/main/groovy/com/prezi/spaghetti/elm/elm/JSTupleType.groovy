package com.prezi.spaghetti.elm.elm

import java.util.List

class JSTupleType implements JSType {
  // The underlying tuple
  private final TupleType d_tupleType;

  TupleType getTupleType() {
    return d_tupleType;
  }

  JSTupleType(TupleType tupleType) {
    d_tupleType = tupleType;
  }

  @Override
  public String elmRep() {
    return "JS.JSObject";
  }  


  @Override
  public Value defaultValue() {
    return d_tupleType.toDictType().defaultValue()
  }  

  @Override
  public Value fromJSFunction() {
    def lambda = "(\\r -> (" +
      [0 .. d_tupleType.types().size() - 1].collect{"r.a" + it}.join(", ") +
    "))";

    return new IdenValue("(" + lambda + " . JS.toRecord)");
  }

  @Override
  public Value toJSFunction() {
    def i = 0;
    def els = d_tupleType.types().collect{def r = "a" + i; i++; return r};
    def lambda = "(\\(" + els.join(", ") + ") -> {" + els.collect{it + " = " + it}.join(", ") +
                 "})";

    return new IdenValue("(JS.fromRecord . " + lambda + ")");
  }

  
}
