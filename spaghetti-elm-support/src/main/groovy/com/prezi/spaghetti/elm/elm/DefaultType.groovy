package com.prezi.spaghetti.elm.elm

import com.prezi.spaghetti.elm.elm.Value

// Types that have default values
interface DefaultType extends Type {

  abstract Value defaultValue();

}
