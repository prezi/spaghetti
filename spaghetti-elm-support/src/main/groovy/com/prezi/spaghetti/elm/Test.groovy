package com.prezi.spaghetti.elm

import com.prezi.spaghetti.elm.elm.*
import java.util.Map

import com.prezi.spaghetti.ModuleConfigurationParser

class Main {

  public static void main (String[] args) {
    def map = [ someInt : new IntType(),
                someBool : new BoolType(),
                someString : new StringType() ];

    def dictType = new DictType(map);
    def jsType = new JSObjectType(dictType);

    def arrowType = new ArrowType(jsType, dictType);


    def moduleHeader = new ModuleHeader("Try", ["hey"]);
    def importList = [new Import("JavaScript", "JS")];
    def jsInt = new JSNumberType(JSNumberType.NumberType.INT);
    def decList = [new ForeignImportDec("asd", dictType.defaultValue(), "js_asd", signal(dictType))];

    def module = new Module(moduleHeader, importList, decList);


    String m = new File("Test.module").text;

    def configCxt = ModuleConfigurationParser.parse(m);
    def config = ModuleConfigurationParser.parse([], [configCxt]);

    def elmGen = new ElmGenerator(config);

    elmGen.generateModuleHeaders(config.localModules.first(), new File("build"));
  }


  public static Type signal(Type t) {
    new AppType(new IdenType("Signal"), [t]);
  }
}
