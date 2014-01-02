package com.prezi.spaghetti.elm

import com.prezi.spaghetti.elm.elm.*
import com.prezi.spaghetti.elm.js.*
import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.NotNull

class ElmModuleGenerator {

  private final ModuleParser.ModuleDefinitionContext d_module;

  private static final s_implSuffix = "Impl";
  private static final s_exportSuffix = "Export";
  private static final s_importSuffix = "Import";
  
  private static final def s_primitiveTypes =
    [ bool: new BoolType(),
      int: new IntType(),
      float: new FloatType(),
      String: new StringType(),
    ];

  ElmModuleGenerator(ModuleParser.ModuleDefinitionContext module) {
    d_module = module;
  }

  public List<Module> generateModules()
  {

    def typeDefCxts = d_module.moduleElement().collect{it.typeDefinition()} - null;

    return typeDefCxts.collect{generateTri(it)}.flatten();
  }

  // returns the (import, export, implementationSkeleton) module triple
  private List<Module> generateTri(@NotNull @NotNull ModuleParser.TypeDefinitionContext ctx)
  {
    def methodDefs = ctx.typeElement().collect{ it.methodDefinition() } - null; // i feel like such a pro groovy hacker

    def foreignFunctions = methodDefs.collect{createForeignFunction(it)};


    def foreignExports = [];
    def foreignImports = [];

    foreignFunctions.each{
      it.elim
      {
        ForeignImportFunction f -> foreignImports.add(f);
      }
      {
        ForeignExportFunction f -> foreignExports.add(f);
      };
    };

    def modules = [createImportModule(foreignImports, ctx.name.text),
                   createExportModule(foreignExports, ctx.name.text),
                   createImplModule(foreignImports, foreignExports, ctx.name.text)];

    return modules;
  }

  private Module createExportModule(List<ForeignFunction> exportFs, String name) {

    def moduleHeader = new ModuleHeader(d_module.name.Name.getText(), name + s_exportSuffix, []);

    def methodNames = exportFs.collect{it.methodName()};

    def imports = [new Import(null, "JavaScript", "JS"),
                   new Import(null, "JavaScript.Experimental", "JS"),
                   new Import(d_module.name.Name.getText(),
                              name + s_implSuffix, s_implSuffix)];

    def main = new FunDec("main", ElmUtils.signal(new IdenType("Element")),
                          new IdenValue(s_implSuffix + ".main"));

    return new Module(moduleHeader, imports, exportFs, [main], true);
  }

  private Module createImportModule(List<ForeignFunction> importFs, String name) {
    def methodNames = importFs.collect{it.methodName()};
    
    def moduleHeader = new ModuleHeader(d_module.name.Name.getText(), name + s_importSuffix, methodNames);

    def imports = [new Import(null, "JavaScript", "JS"),
                   new Import(null, "JavaScript.Experimental", "JS")];


    return new Module(moduleHeader, imports, importFs, [], true);
  }

  // Creates a skeleton implementation module
  private Module createImplModule(List<ForeignFunction> importFs, List<ForeignFunction> exportFs, String name) {
    def exportMethodNames = exportFs.collect{it.methodName()};
    def importMethodNames = importFs.collect{it.methodName()};

    def moduleHeader = new ModuleHeader(d_module.name.Name.getText(), name + s_implSuffix, ["main"] + exportMethodNames);

    def imports = [new Import(d_module.name.Name.getText(), name + s_importSuffix)];

    def main = new FunDec("main", ElmUtils.signal(new IdenType("Element")),
                 new AppValue(
                   new IdenValue("constant"),
                   new AppValue(
                     new IdenValue("plainText"),
                     new StringValue("Hello World! I AM ELM SKELETON"))));

    def skels = exportFs.collect{
      new FunDec(it.methodName(), ElmUtils.signal(it.ifaceType()),
                 ElmUtils.defaultSignalValue(it.ifaceType()));
    };

    return new Module(moduleHeader, imports, [], [main] + skels, false);
  }

  // private static IfaceType createIfaceType(ModuleParser.MethodDefinitionContext method) {

  //   // Check if return type is not void
  //   def voidType = (VoidReturnTypeChainContext) method.returnTypeChain();
  //   if (!voidType) {
  //     throw new ElmException("Return type of method '" + method.name + "' may only be void");
  //   }
    
  //   def typeNamePairs = method.typeNamePairs().elements;

  //   // This is a special case. The argument may be a single callback function if the signal is to be exported and if it's not then we don't need to generate a dictionary.
  //   if (typeNamePairs.size() == 1) {
  //     def type = typeNamePairs.first().type;

  //     // Check if it's a callback
  //     def callbackType = (CallbackTypeChainContext) type;
  //     if (callbackType) {
  //       return createCallbackType(type);
  //     }
  //     return createPrimitiveType(type);
  //   } else {
  //     def dict = typeNamePairs.collectEntries{
  //       return [it.Name().getText(), createPrimitiveType(it.type)];
  //     }

  //     return new DictType(dict);
  //   }
  // }

  private ForeignFunction createForeignFunction(ModuleParser.MethodDefinitionContext method) {

    // Check void return
    if (!(method.returnTypeChain() instanceof ModuleParser.VoidReturnTypeChainContext)) {
      throw new ElmException("Return type of method '" + method.name + "' may only be void");
    }

    def typeNamePairs = method.typeNamePairs().elements;

    // If it's a callback create an export
    if (typeNamePairs.size() == 1 && typeNamePairs.first().type instanceof ModuleParser.CallbackTypeChainContext) {
      def callbackType = (ModuleParser.CallbackTypeChainContext) typeNamePairs.first().type;

      // Gather arguments and return type
      def types = callbackType.returnType();
      def args = types[0 .. types.size() - 2];
      def retType = types.last();

      // Check void return of callback
      if (!retType.voidType()) {
        throw new ElmException("Return type of method '" + method.name + "' may only be " +
                               "void");
      }

      def valueTypes = args.collect{
        def ret = it.valueType();
        if (!ret) {
          def callbackName = typeNamePairs.first().name;
          throw new ElmException("Argument types of callback '" + callbackName + "' " +
                                 "cannot be void");
        }
        return ret;
      };

      
      def ifaceType = createExportIfaceType(valueTypes);
      return new ForeignExportFunction(ifaceType, method.name.getText(), s_implSuffix);
      
    } else {

      // It's an imported signal
      def valueTypes = typeNamePairs.collect{
        if (!(it.type instanceof ModuleParser.NormalValueTypeChainContext)) {
          throw new ElmException("You may either specify a single callback type or several non-callback types");
        }
        def normalType = (ModuleParser.NormalValueTypeChainContext) it.type;
        return normalType.valueType();
      };

      def ifaceType = createImportIfaceType(typeNamePairs.collect{it.Name().getText()}, valueTypes);
      return new ForeignImportFunction(ifaceType, method.name.getText());
    }
    
  }

  private IfaceType createExportIfaceType(List<ModuleParser.ValueTypeContext> types) {
    
    if (types.size() == 1) {
      return createPrimitiveType(types.first());
    } else {
      return new TupleType(types.collect{createPrimitiveType(it)});
    }
  }

  private IfaceType createImportIfaceType(List<String> names, List<ModuleParser.ValueTypeContext> types) {

    // If it's a single type no need to create a dictionary
    if (types.size() == 1) {
      return createPrimitiveType(types.first());
    } else {
      def dict = [names, types].transpose().collectEntries{
        return [it[0], createPrimitiveType(it[1])];
      }

      return new DictType(dict);
    }
  }

  private IfaceType createPrimitiveType(ModuleParser.ValueTypeContext type) {

    def exc = {
      throw new ElmException("Type '" + type + "' is unsupported, you may only " +
                             "use primitive types that can interface with Elm");
    };

    def primType = type.primitiveType();
    if (!primType) {
      exc();
    }

    def ifaceType = s_primitiveTypes.get(primType.text);
    if (!ifaceType) {
      exc();
    }

    return ifaceType;
  }


  private ForeignImportDec createForeignImport(String methodName, IfaceType type) {
    def jsType = type.toJSType();
    def defaultValue = jsType.defaultValue();
    def internalName = "js_" + methodName;

    return new ForeignImportDec(methodName, defaultValue, internalName, signal(jsType));
  }

  private FunDec createExportFunctionDec(String methodName, IfaceType type) {
    def functionBody =
      new AppValue(
        new AppValue(
          new IdenValue("lift"),
          type.fromJSFunction()),
        new IdenValue("js_" + methodName));

    return new FunDec(methodName, ElmUtils.signal(type), functionBody);
  }
}

