package com.prezi.spaghetti.elm

import com.prezi.spaghetti.Generator
import com.prezi.spaghetti.GeneratorFactory
import com.prezi.spaghetti.ModuleConfiguration

public class ElmGeneratorFactory implements GeneratorFactory {

  @Override
  String getPlatform()
  {
    return "elm";
  }

  @Override
  Generator createGenerator(ModuleConfiguration configuration)
  {
    return new ElmGenerator(configuration);
  }
}
