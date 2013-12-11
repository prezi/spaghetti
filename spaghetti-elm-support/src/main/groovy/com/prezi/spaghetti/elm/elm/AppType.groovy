package com.prezi.spaghetti.elm.elm


class AppType implements Type {
  private final Type d_fun

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