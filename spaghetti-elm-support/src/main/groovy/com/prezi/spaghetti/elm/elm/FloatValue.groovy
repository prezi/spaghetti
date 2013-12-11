package com.prezi.spaghetti.elm.elm

import com.prezi.spaghetti.elm.elm.Value

import java.util.ArrayList

class FloatValue implements Value {
  private final float d_float;

  FloatValue(float i) {
    this.d_float = i;
  }

  public float getFloat() {
    return this.d_float;
  }

  @Override
  public String elmRep() {
    return Float.toString(d_float);
  }
}
