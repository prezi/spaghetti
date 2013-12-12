package com.prezi.spaghetti.elm.elm

class Import implements ElmRep {
  private final String d_moduleName;
  private final String d_qualified; // may be null

  Import(String moduleName, String qualified) {
    d_moduleName = moduleName;
    d_qualified = qualified;
  }

  Import(String moduleName) {
    d_moduleName = moduleName;
    d_qualified = null;
  }

  @Override
  public String elmRep() {

    def ret = "";

    ret += "import " + d_moduleName;
    if (d_qualified != null) {
      ret += " as " + d_qualified;
    }
    ret += "\n";

    return ret;
  }
}