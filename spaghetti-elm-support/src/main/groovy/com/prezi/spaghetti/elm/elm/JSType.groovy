package com.prezi.spaghetti.elm.elm

interface JSType extends DefaultType {
  Value fromJSFunction();
  Value toJSFunction();
}
