package com.prezi.spaghetti.gradle

import com.prezi.spaghetti.AbstractModuleVisitor
import com.prezi.spaghetti.FQName
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.ModuleUtils
import com.prezi.spaghetti.grammar.ModuleParser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.misc.NotNull

import java.util.Set

class SymbolCollectVisitor extends AbstractModuleVisitor<Set<String>> {

  public SymbolCollectVisitor(ModuleDefinition module)
  {
    super(module);
  }

  @Override
  protected Set<String> aggregateResult(Set<String> aggregate, Set<String> nextResult) {
    return aggregate + nextResult;
  }

  @Override
  protected Set<String> defaultResult() {
    return [];
  }

  // @Override
  // public Set<String> visitQualifiedName(@NotNull ModuleParser.QualifiedNameContext ctx) {

  //   return ctx.parts.collect{it.getText()};
  // }

  @Override
  public Set<String> visitExternTypeDefinition(@NotNull ModuleParser.ExternTypeDefinitionContext ctx) {

    ctx.name.parts.collect{it.getText()};
  }

  @Override
  public Set<String> visitMethodDefinition(@NotNull @NotNull ModuleParser.MethodDefinitionContext ctx){

    return [ctx.name.getText()];
  }

  @Override
  public Set<String> visitStructDefinition(@NotNull ModuleParser.StructDefinitionContext ctx) {

    return ctx.propertyDefinition().collect{it.property.name.getText()};
  }

  @Override
  public Set<String> visitConstDefinition(@NotNull ModuleParser.ConstDefinitionContext ctx) {

    return [ctx.name.getText()] + ctx.propertyDefinition().collect{it.property.name.getText()};
  }

  // @Override
  // public Set<String> visitPropertyDefinition(@NotNull @NotNull ModuleParser.PropertyDefinitionContext ctx){

    
  // }

  // @Override
  // public Set<String> visitTypeNamePairs(@NotNull @NotNull ModuleParser.TypeNamePairsContext ctx){
  // }
  
  // @Override
  // public Set<String> visitTypeNamePair(@NotNull @NotNull ModuleParser.TypeNamePairContext ctx){
  //   return [ctx.name.getText()] + visitChildren(ctx);
  // }

	// @Override public T visitTypeChain(@NotNull ModuleParser.TypeChainContext ctx) { return visitChildren(ctx); }

  // @Override
  // public Set<String> visitCallbackTypeChain(@NotNull @NotNull ModuleParser.CallbackTypeChainContext ctx){
  //   TODO;
  // }

  // @Override
  // public Set<String> visitValueType(@NotNull @NotNull ModuleParser.ValueTypeContext ctx){
  //   TODO;
  // }

  // @Override
  // public Set<String> visitModuleType(@NotNull @NotNull ModuleParser.ModuleTypeContext ctx){
  //   TODO;
  // }

  // @Override
  // public Set<String> visitTypeParameters(@NotNull @NotNull ModuleParser.TypeParametersContext ctx){
  //   TODO;
  // }

  // @Override
  // public Set<String> visitTypeParameter(@NotNull @NotNull ModuleParser.TypeParameterContext ctx){
  //   TODO;
  // }

  // @Override
  // public Set<String> visitTypeArguments(@NotNull @NotNull ModuleParser.TypeArgumentsContext ctx){
  //   TODO;
  // }

  // @Override
  // public Set<String> visitVoidType(@NotNull @NotNull ModuleParser.VoidTypeContext ctx){
  //   TODO;
  // }

  // @Override
  // public Set<String> visitPrimitiveType(@NotNull @NotNull ModuleParser.PrimitiveTypeContext ctx){
  //   TODO;
  // }


}