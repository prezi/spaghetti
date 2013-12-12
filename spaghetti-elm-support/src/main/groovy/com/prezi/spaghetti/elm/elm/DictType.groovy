package com.prezi.spaghetti.elm.elm

import java.util.Map
import java.util.HashMap

class DictType implements IfaceType {

  private Map<String, Type> d_map;

  DictType(Map<String, Type> map) {
    d_map = map;
  }

  // ! Will throw if dict contains non-defaultable type
  // The issue is that a Dictionary of say functions is a valid Elm type, but it's not defaultable. We could parameterise DictType by the types of types (Type or DefaultType), however we cannot say only DictType<DefaultType> implements DefaultType... balls
  @Override
  public Value defaultValue() {

    def valueMap = new HashMap<String, Value>();

    for (e in d_map) {

      def defType = (DefaultType) e.value; // i feel so dirty
      valueMap.put(e.key, defType.defaultValue());
    }

    return new DictValue(valueMap);
  }  

  @Override
  public String elmRep() {

    def ret = "";

    ret += "{";

    def it = d_map.entrySet().iterator();
    while (it.hasNext()) {

      def e = it.next();
      ret += e.key + " : " + e.value.elmRep();

      if (it.hasNext()) {
        ret += ", ";
      }
    }

    ret += "}";

    return ret;
  }  
  
  @Override
  public Type toJSType() {
    return new JSObjectType(this);
  }

  @Override
  public Value fromJSFunction() {
    return new IdenValue("JS.toRecord");
  }

}

