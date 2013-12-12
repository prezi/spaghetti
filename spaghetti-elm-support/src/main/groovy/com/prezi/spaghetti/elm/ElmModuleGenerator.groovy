package com.prezi.spaghetti.elm

import com.prezi.spaghetti.elm.elm.*
import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.NotNull

class ElmModuleGenerator {

  private static ModuleParser.ModuleDefinitionContext d_module;
  
  private static final def PRIMITIVE_TYPES =
    [ bool: new BoolType(),
      int: new IntType(),
      float: new FloatType(),
      String: new StringType(),
    ];

  ElmModuleGenerator(ModuleParser.ModuleDefinitionContext module) {
    d_module = module;
  }
  
  @Override
  public static List<Module> generateModules()
  {

    def typeDefCxts = d_module.moduleElement().collect{it.typeDefinition()} - null;

    return typeDefCxts.collect{return generateModule(it);};
  }

  @Override
  private static Module generateModule(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
  {
    def elmModuleName = d_module.name.Name.getText() + "." + ctx.name.text;
    def methodDefs = ctx.typeElement().collect{ it.methodDefinition() } - null; // i feel like such a pro groovy hacker
    def methodNames = methodDefs.collect{ it.Name().getText() };
    def moduleHeader = new ModuleHeader(elmModuleName, methodNames);

    def ifaceTypes = methodDefs.collect{return createIfaceType(it);};


    def nameTypePairs = [methodNames, ifaceTypes].transpose();
    def foreignImports = nameTypePairs.collect{
      return createForeignImport(it[0], it[1]);
    };

    def functionDecs = nameTypePairs.collect{
      return createFunctionDec(it[0], it[1]);
    };

    def imports = [new Import("JavaScript", "JS"),
                   new Import("JavaScript.Experimental", "JS")];

    return new Module(moduleHeader, imports, foreignImports + functionDecs);
  }

  private static IfaceType createIfaceType(ModuleParser.MethodDefinitionContext method) {
    // TODO warn if return type is not void

    def dict = method.typeNamePairs().elements.collectEntries{
      def primType = ((ModuleParser.NormalValueTypeChainContext) it.type).valueType().primitiveType();
      IfaceType type = PRIMITIVE_TYPES.get(primType.text);

      return [it.Name().getText(), type];
    }

    return new DictType(dict);
  }


  private static ForeignImportDec createForeignImport(String methodName, IfaceType type) {
    def jsType = type.toJSType();
    def defaultValue = jsType.defaultValue();
    def internalName = "js_" + methodName;

    return new ForeignImportDec(methodName, defaultValue, internalName, signal(jsType));
  }

  private static FunDec createFunctionDec(String methodName, IfaceType type) {
    def functionBody =
      new AppValue(
        new AppValue(
          new IdenValue("lift"),
          type.fromJSFunction()),
        new IdenValue("js_" + methodName));

    return new FunDec(methodName, signal(type), functionBody);
  }

  public static Type signal(Type t) {
    new AppType(new IdenType("Signal"), [t]);
  }
}

