package com.prezi.spaghetti.elm.elm

import java.util.Map

class Main {

  public static void main (String[] args) {
    def map = [ someInt : new IntType(),
                someBool : new BoolType(),
                someString : new StringType() ];

    def dictType = new DictType(map);
    def jsType = new JSObjectType(dictType);

    def arrowType = new ArrowType(jsType, dictType);


    def moduleHeader = new ModuleHeader("Try", []);
    def importList = [new Import("JavaScript", "JS")];
    def jsInt = new JSNumberType(JSNumberType.NumberType.INT);
    def decList = [new ForeignImportDec("asd", jsInt.defaultValue(), "js_asd", signal(jsInt))];

    def module = new Module(moduleHeader, importList, decList);

    System.out.println(module.elmRep());
  }


  public static Type signal(Type t) {
    new AppType(new IdenType("Signal"), [t]);
  }
}
