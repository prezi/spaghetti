package com.prezi.spaghetti.elm.elm

import com.prezi.spaghetti.elm.elm.Value

import java.util.ArrayList

class StringValue implements Value {

  private final String d_str;

  StringValue(String str) {
    this.d_str = str;
  }

  public String getString() {
    return this.d_str;
  }

  private static String escapeStr (String s) {
    return s.collectReplacements{
      switch (it) {
      case "\"" : return "\\\"";
      case "\\" : return "\\\\";
      case "\n" : return "\\n";
      case "\t" : return "\\t";
      }
      return null;
    };
  }

  @Override
  public List<String> elmRep() {
    def ret = new ArrayList<String>();
    ret.add("\"" + escapeStr(d_str) + "\"");
    return ret;
  }
}
