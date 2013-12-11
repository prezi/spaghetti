package com.prezi.spaghetti.elm.elm

import com.prezi.spaghetti.elm.elm.Value

import java.util.ArrayList

class IntValue implements Value {
  private final int d_int;

  IntValue(int i) {
    this.d_int = i;
  }

  public int getInt() {
    return this.d_int;
  }

  @Override
  public String elmRep() {
    return Integer.toString(d_int);
  }
}