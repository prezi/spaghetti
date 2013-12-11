package com.prezi.spaghetti.elm.elm

import java.util.Map

class Main {

  public static void main (String[] args) {
    def map = [ someInt : new IntType(),
                someBool : new BoolType(),
                someString : new StringType()];

    def dictType = new DictType(map);
    def jsType = new JSObjectType(dictType);

    def arrowType = new ArrowType(jsType, dictType);

    System.out.println(jsType.elmRep());
    System.out.println(jsType.defaultValue().elmRep());
    System.out.println(new AppType(new IdenType("Signal"), [arrowType]).elmRep());
  }
}
