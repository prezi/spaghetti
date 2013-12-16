package com.prezi.spaghetti.elm.elm

import com.prezi.spaghetti.elm.elm.Value

import java.util.ArrayList
import java.util.Map


class DictValue implements Value {
  
  private final Map<String, Value> d_map;

  DictValue(Map<String, Value> map) {
    d_map = map;
  }

  @Override
  public String elmRep() {

    return "{" + d_map.collect{it.key + " = " + e.value.elmRep()}.join(", ") + "}";
  }
}
