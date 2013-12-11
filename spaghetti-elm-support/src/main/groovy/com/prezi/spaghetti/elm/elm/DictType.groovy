package com.prezi.spaghetti.elm.elm

import java.util.Map

class DictType implements DefaultType, Type {

  private Map<String, Type> d_map;

  DictType(Map<String, Type> map) {
    d_map = map;
  }

  // ! Will throw if dict contains non-defaultable type
  // The issue is that a Dictionary of say functions is a valid Elm type, but it's not defaultable. We could parameterise DictType by the types of types (Type or DefaultType), however we cannot say only DictType<DefaultType> implements DefaultType... balls
  @Override
  public Value defaultValue() {

    def valueMap = new Map<String, Value>();

    for (e in d_map) {

      def defType = (DefaultType) e.value; // i feel so dirty
      valueMap.put(e.key, defType.defaultValue());
    }

    return new DictValue(valueMap);
  }  

  @Override
  public List<String> elmRep() {

    def ret = new ArrayList<String>();

    ret.add("{");

    for (e in d_map) {

      ret.add(e.key + " : " + e.value.elmRep());

      if (e.hasNext()) {
        ret.add(",");
      }
    }

    ret.add("}");

    return ret;
  }  
  
}

