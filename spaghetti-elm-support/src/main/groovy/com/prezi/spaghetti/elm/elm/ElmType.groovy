package com.prezi.spaghetti.elm.elm

// This class contains all elm types
class ElmType {

  public class BoolType implements IfaceType {

    BoolType() {
    }

    @Override
    public Value defaultValue() {
      return new BoolValue(false);
    }  

    @Override
    public String elmRep() {
      return "Bool";
    }

    @Override
    public Type toJSType() {
      return new JSBoolType();
    }

    @Override
    public Value fromJSFunction() {
      return new IdenValue("JS.toBool");
    }
  }

  public class IntType implements IfaceType {

    IntType() {
    }

    @Override
    public Value defaultValue() {
      return new IntValue(0);
    }  

    @Override
    public String elmRep() {
      return "Int";
    }  

    @Override
    public Type toJSType() {
      return new JSNumberType(JSNumberType.INT);
    }

    @Override
    public Value fromJSFunction() {
      return new IdenValue("JS.toInt");
    }
  }

  public class FloatType implements IfaceType {

    FloatType() {
    }

    @Override
    public Value defaultValue() {
      return new FloatValue(0.0);
    }  

    @Override
    public String elmRep() {
      return "Float";
    }  

    @Override
    public Type toJSType() {
      return new JSNumberType(JSNumberType.FLOAT);
    }

    @Override
    public Value fromJSFunction() {
      return new IdenValue("JS.toFloat");
    }
  }

  public class StringType implements IfaceType {

    StringType() {
    }

    @Override
    public Value defaultValue() {
      return new StringValue("");
    }  

    @Override
    public String elmRep() {
      return "String";
    }  

    @Override
    public Type toJSType() {
      return new JSStringType();
    }

    @Override
    public Value fromJSFunction() {
      return new IdenValue("JS.toString");
    }
  }

  public class DictType implements IfaceType {

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

  public class ArrowType implements Type {
    private final Type d_fromType, d_toType;

    ArrowType(Type fromType, Type toType) {
      d_fromType = fromType;
      d_toType = toType;
    }

    @Override
    public String elmRep() {
      return "(" + d_fromType.elmRep() + " -> " + d_toType.elmRep() + ")";
    }

    public
  }
  
  class AppType implements Type {
    private final Type d_fun;

    private final List<Type> d_argList; // Must do all type args at once, partial application doesn't work in Elm (e.g. "((Either Int) String)")

    AppType(Type fun, List<Type> argList) {
      d_fun = fun;
      d_argList = argList;
    }

    @Override
    public String elmRep() {

      def ret = "";

      ret += "(" + d_fun.elmRep() + " ";

      def i = d_argList.iterator();
      while (i.hasNext()) {
        def ty = i.next();
        ret += ty.elmRep();
        if (i.hasNext()) {
          ret += " ";
        }
      }

      ret += ")";

      return ret;
    }  
  
  }

  public class IdenType implements Type {

    private final String d_idenStr;

    IdenType (String idenStr) {
      d_idenStr = idenStr;
    }

    @Override
    public String elmRep() {
      return d_idenStr;
    }  
  }

  // JS types
  public class JSBoolType implements DefaultType, Type {

    JSBoolType() {
    }

    @Override
    public Value defaultValue() {
      return new AppValue (new IdenValue ("JS.fromBool"), new BoolType().defaultValue());
    }  

    @Override
    public String elmRep() {
      return "JS.JSBool";
    }

  }

  public class JSStringType implements DefaultType, Type {

    JSStringType() {
    }

    @Override
    public Value defaultValue() {
      return new AppValue (new IdenValue ("JS.fromString"), new StringType().defaultValue());
    }  

    @Override
    public String elmRep() {
      return "JS.JSString";
    }  
  }

  public class JSNumberType implements DefaultType, Type {

    public enum NumberType {
      INT, FLOAT                 // Float not used for now
    }

    private final NumberType d_numberType

    JSNumberType(NumberType numberType) {
      d_numberType = numberType;
    }

    @Override
    public Value defaultValue() {
      switch (d_numberType) {
      case NumberType.INT: return new AppValue (new IdenValue ("JS.fromInt"), new IntType().defaultValue());
      case NumberType.FLOAT: return new AppValue (new IdenValue ("JS.fromFloat"), new FloatType().defaultValue());
      }
    }  

    @Override
    public String elmRep() {
      return "JS.JSNumber";
    }  
  }

  public class JSObjectType implements DefaultType, Type {

    // The underlying dictionary
    private final DictType d_dictType;

    DictType getDictType() {
      return d_dictType;
    }

    JSObjectType(DictType dictType) {
      d_dictType = dictType;
    }

    @Override
    public Value defaultValue() {
      return new AppValue (new IdenValue ("JS.fromRecord"), d_dictType.defaultValue());
    }  

    @Override
    public String elmRep() {
      return "JS.JSObject";
    }  
  }

}
