package com.prezi.spaghetti.elm.elm

// A ForeignFunction may be a foreign import or an export and can be used to generate the foreign declaration as well as an elm-typed function for internal use.
abstract class ForeignFunction implements ElmRep {
  abstract String generateJS(String elmIface);          // generates "userLeft : function(a){__elm.send('userLeft', a);}," or callback registering
  abstract IfaceType ifaceType();
  abstract String methodName();

  // pattern matching groovy style. sry but OOP sucks
  public Object elim(Closure importElim, Closure exportElim) {
    if (this instanceof ForeignImportFunction) {
      return importElim((ForeignImportFunction) this);
    } else {
      return exportElim((ForeignExportFunction) this);
    }
  }

}
