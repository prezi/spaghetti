package com.prezi.spaghetti.elm.elm

// A ForeignFunction may be a foreign import or an export and can be used to generate the foreign declaration as well as an elm-typed function for internal use.
interface ForeignFunction {
  Dec foreignDec();
  FunDec functionDec();
}
