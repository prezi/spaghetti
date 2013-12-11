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

    def ret = "";

    ret += "{";

    def it = d_map.entrySet().iterator();
    while (it.hasNext()) {

      def e = it.next();
      ret += e.key + " = " + e.value.elmRep();

      if (it.hasNext()) {
        ret += ", ";
      }
    }

    ret += "}";

    return ret;
  }
}
