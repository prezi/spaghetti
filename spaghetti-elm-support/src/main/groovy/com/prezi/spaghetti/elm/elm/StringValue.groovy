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
    return collectReplacements(s, {
      switch (it) {
      case "\"" : return "\\\"";
      case "\\" : return "\\\\";
      case "\n" : return "\\n";
      case "\t" : return "\\t";
      }
      return null;
    });
  }

  @Override
  public String elmRep() {
    return "\"" + escapeStr(d_str) + "\"";
  }

  // gradle uses Groovy 1.8.6 which doesn't have collectReplacements...
  private static String collectReplacements(String s, Closure transform) {
    def ss = s.collect{
      def r = transform(it);
      if (r) {
        return r;
      } else {
        return it
      }
    }
    return ss.join();
  }
}
