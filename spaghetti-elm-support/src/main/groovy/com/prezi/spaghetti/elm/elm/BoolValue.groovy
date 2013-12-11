package com.prezi.spaghetti.elm.elm

import com.prezi.spaghetti.elm.elm.Value

import java.util.ArrayList

class BoolValue implements Value {
  private final bool d_bool;

  BoolValue(bool b) {
    this.d_bool = b;
  }

  public bool getBool() {
    return this.d_bool;
  }

  @Override
  public List<String> elmRep() {
    def ret = new ArrayList<String>();
    ret.add(d_bool ? "True" : "False");
    return ret;
  }
}
