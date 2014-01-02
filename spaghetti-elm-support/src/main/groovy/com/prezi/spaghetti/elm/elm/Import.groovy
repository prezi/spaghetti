package com.prezi.spaghetti.elm.elm

class Import implements ElmRep {
  private final String d_packageName; // may be null
  private final String d_moduleName;
  private final String d_qualified; // may be null
  private final boolean d_isOpen;

  Import(String packageName, String moduleName, String qualified) {
    d_moduleName = moduleName;
    d_qualified = qualified;
    d_isOpen = false;
    d_packageName = packageName;
  }

  Import(String packageName, String moduleName) {
    d_moduleName = moduleName;
    d_qualified = null;
    d_isOpen = true;
    d_packageName = packageName;
  }

  @Override
  public String elmRep() {

    def ret = "";

    ret += "import ";
    if (d_isOpen) {
      ret += "open ";
    }
    if (d_packageName != null) {
      ret += d_packageName + ".";
    }
    ret += d_moduleName;
    if (d_qualified != null) {
      ret += " as " + d_qualified;
    }
    ret += "\n";

    return ret;
  }
}