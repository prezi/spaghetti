package com.prezi.spaghetti.elm

class ElmException extends RuntimeException {
  ElmException(String e) {
    super("Exception while generating Elm interface: " + e);
  }
}
