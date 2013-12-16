package com.prezi.spaghetti.elm.elm

import java.util.List

class TupleValue implements Value {

  private final List<Value> d_values;

  TupleValue(List<Value> values) {
    d_values = values;
  }

  @Override
  public String elmRep() {
    return "(" + d_values.collect{it.elmRep()}.join(", ") + ")"
  }  
}