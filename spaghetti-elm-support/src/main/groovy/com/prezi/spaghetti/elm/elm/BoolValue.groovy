package com.prezi.spaghetti.elm.elm

import com.prezi.spaghetti.elm.elm.Value

import java.util.ArrayList

class BoolValue implements Value {

  private final boolean d_bool;

  BoolValue(boolean b) {
    this.d_bool = b;
  }

  public boolean getBool() {
    return this.d_bool;
  }

  @Override
  public String elmRep() {
    return (d_bool ? "True" : "False");
  }
}
