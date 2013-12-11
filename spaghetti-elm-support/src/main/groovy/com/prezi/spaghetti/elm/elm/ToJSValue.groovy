package com.prezi.spaghetti.elm.elm

interface ToJSValue {
  // returns the converting function(!). This should be static but theres no such thing in java
  Value toJSValueFun();
}
