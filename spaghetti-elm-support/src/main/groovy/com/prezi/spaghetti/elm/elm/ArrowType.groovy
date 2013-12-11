package com.prezi.spaghetti.elm.elm

class ArrowType implements Type {
  private final Type d_fromType, d_toType;

  ArrowType(Type fromType, Type toType) {
    d_fromType = fromType;
    d_toType = toType;
  }

  @Override
  public String elmRep() {
    return "(" + d_fromType.elmRep() + " -> " + d_toType.elmRep() + ")";
  }
}