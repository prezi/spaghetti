package com.prezi.spaghetti.elm.elm

// A ForeignFunction may be a foreign import or an export and can be used to generate the foreign declaration as well as an elm-typed function for internal use.
interface ForeignFunction {
  String generateJS(String elmIface);          // generates "userLeft : function(a){__elm.send('userLeft', a);}," or callback registering
  Dec foreignDec();
  FunDec functionDec();
}
