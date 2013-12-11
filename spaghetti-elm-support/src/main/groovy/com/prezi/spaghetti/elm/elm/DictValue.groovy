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
  public List<String> elmRep() {

    def ret = new ArrayList<String>();

    ret.add("{");
    for (e in d_map) {

      ret.add(e.key + " = " + e.value.elmRep());
      if (e.hasNext()) {

        ret.add(",");
      }
    }
    ret.add("}");

    return ret;
  }
}
