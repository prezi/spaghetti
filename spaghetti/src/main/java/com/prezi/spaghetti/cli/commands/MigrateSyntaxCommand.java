package com.prezi.spaghetti.cli.commands;

import com.google.common.base.Joiner;
import com.prezi.spaghetti.definition.internal.DefaultModuleDefinitionSource;
import com.prezi.spaghetti.definition.internal.ModuleDefinitionParser;
import com.prezi.spaghetti.internal.grammar.ModuleParser;
import io.airlift.command.Command;

// TODO [knuton] Need to take care of single-asterisk comments, they'll probably be eaten by parser

@Command(name = "migrate", description = "Generates new Spaghetti syntax from old format")
public class MigrateSyntaxCommand extends AbstractDefinitionAwareCommand {

	@Override
	public Integer call() throws Exception {
		System.out.println(
				prettyPrint(ModuleDefinitionParser.parseLegacy(DefaultModuleDefinitionSource.fromFile(definition))));
		return 0;
	}

	private static String prettyPrint(ModuleParser.ModuleDefinitionLegacyContext moduleDefCtx) {
		StringBuilder moduleDef = new StringBuilder();
		moduleDef.append("module ").append(qualifiedName(moduleDefCtx.qualifiedName()));
		if (moduleDefCtx.Name() != null) moduleDef.append(" as ").append(moduleDefCtx.Name()).append(" {\n");
		for (ModuleParser.ModuleElementLegacyContext moduleElem : moduleDefCtx.moduleElementLegacy()) {
			// extern types
			if (moduleElem.externTypeDefinitionLegacy() != null) {
				newline(moduleDef);
				ModuleParser.ExternInterfaceDefinitionLegacyContext extTyDef =
						moduleElem.externTypeDefinitionLegacy().externInterfaceDefinitionLegacy();
				generateMeta(extTyDef.documentation, extTyDef.annotations(), moduleDef, 1);
				indent(moduleDef, 1);
				moduleDef.append("extern interface "
				).append(
						qualifiedName(extTyDef.qualifiedName())
				).append(
						extTyDef.typeParameters() != null ? typeParameters(extTyDef.typeParameters()) : ""
				).append(
						";"
				);
			// imports
			} else if (moduleElem.importDeclaration() != null) {
				indent(moduleDef, 1);
				moduleDef.append("import ").append(qualifiedName(moduleElem.importDeclaration().qualifiedName())).append(";");
			// interfaces, structs, enums, consts
			} else if (moduleElem.typeDefinitionLegacy() != null) {
				newline(moduleDef);
				ModuleParser.TypeDefinitionLegacyContext typeDefinition = moduleElem.typeDefinitionLegacy();
				// interface
				if (typeDefinition.interfaceDefinitionLegacy() != null) {
					ModuleParser.InterfaceDefinitionLegacyContext interfaceDefinition = typeDefinition.interfaceDefinitionLegacy();
					generateMeta(interfaceDefinition.documentation, interfaceDefinition.annotations(), moduleDef, 1);
					indent(moduleDef, 1);
					moduleDef.append(
							"interface "
					).append(
							interfaceDefinition.Name()
					);
					if (interfaceDefinition.typeParameters() != null) {
						moduleDef.append(typeParameters(interfaceDefinition.typeParameters()));
					}
					if (interfaceDefinition.superTypeDefinitionLegacy() != null && interfaceDefinition.superTypeDefinitionLegacy().size() > 0) {
						moduleDef.append(" extends ");
						boolean sep = false;
						for (ModuleParser.SuperTypeDefinitionLegacyContext superType : interfaceDefinition.superTypeDefinitionLegacy()) {
							if (sep) moduleDef.append(", ");
							moduleDef.append(
									qualifiedName(superType.qualifiedName())
							).append(
									superType.typeArgumentsLegacy() != null ? typeArguments(superType.typeArgumentsLegacy()) : ""
							);
							sep = true;
						}
					}
					space(moduleDef);
					moduleDef.append("{");
					for (ModuleParser.MethodDefinitionLegacyContext methodDef : interfaceDefinition.methodDefinitionLegacy()) {
						newline(moduleDef);
						moduleDef.append(methodDefinition(methodDef, 2));
					}
					newline(moduleDef);
					indent(moduleDef, 1);
					moduleDef.append("}");
				}
				// struct
				else if (typeDefinition.structDefinitionLegacy() != null) {
					ModuleParser.StructDefinitionLegacyContext structDefinition = typeDefinition.structDefinitionLegacy();
					generateMeta(structDefinition.documentation, structDefinition.annotations(), moduleDef, 1);
					indent(moduleDef, 1);
					moduleDef.append(
							"struct "
					).append(
							structDefinition.Name()
					);
					if (structDefinition.typeParameters() != null) {
						moduleDef.append(typeParameters(structDefinition.typeParameters()));
					}
					if (structDefinition.superTypeDefinitionLegacy() != null) {
						ModuleParser.SuperTypeDefinitionLegacyContext superType = structDefinition.superTypeDefinitionLegacy();
						moduleDef.append(" extends ");
						moduleDef.append(
								qualifiedName(superType.qualifiedName())
						).append(
								superType.typeArgumentsLegacy() != null ? typeArguments(superType.typeArgumentsLegacy()) : ""
						);
					}
					space(moduleDef);
					moduleDef.append("{");
					for (ModuleParser.StructElementDefinitionLegacyContext structElem : structDefinition.structElementDefinitionLegacy()) {
						newline(moduleDef);
						if (structElem.methodDefinitionLegacy() != null) {
							moduleDef.append(methodDefinition(structElem.methodDefinitionLegacy(), 2));
						} else {
							moduleDef.append(propertyDefinition(structElem.propertyDefinitionLegacy()));
						}
					}
					newline(moduleDef);
					indent(moduleDef, 1);
					moduleDef.append("}");
				}
				// enum
				else if (typeDefinition.enumDefinitionLegacy() != null) {
					ModuleParser.EnumDefinitionLegacyContext enumDefinition = typeDefinition.enumDefinitionLegacy();
					generateMeta(enumDefinition.documentation, enumDefinition.annotations(), moduleDef, 1);
					indent(moduleDef, 1);
					moduleDef.append(
							"enum "
					).append(
							enumDefinition.Name()
					).append(
							" {"
					);
					boolean sep = false;
					for (ModuleParser.EnumValueContext enumValue : enumDefinition.enumValue()) {
						if (sep) moduleDef.append(",");
						sep = true;
						newline(moduleDef);
						generateMeta(enumValue.documentation, enumValue.annotations(), moduleDef, 2);
						indent(moduleDef, 2);
						moduleDef.append(enumValue.Name());
						if (enumValue.Integer() != null) {
							moduleDef.append(" = ").append(enumValue.value.getText());
						};
					}
					newline(moduleDef);
					indent(moduleDef, 1);
					moduleDef.append("}");
				}
				// const
				else if (typeDefinition.constDefinitionLegacy() != null) {
					ModuleParser.ConstDefinitionLegacyContext constDefinition = typeDefinition.constDefinitionLegacy();
					generateMeta(constDefinition.documentation, constDefinition.annotations(), moduleDef, 1);
					indent(moduleDef, 1);
					moduleDef.append(
							"const "
					).append(
							constDefinition.Name()
					).append(
							" {"
					);
					for (ModuleParser.ConstEntryLegacyContext constEntry : constDefinition.constEntryLegacy()) {
						newline(moduleDef);
						generateMeta(constEntry.documentation, constEntry.annotations(), moduleDef, 2);
						indent(moduleDef, 2);
						moduleDef.append(constEntry.constEntryDeclLegacy().Name());
						if (constEntry.constEntryDeclLegacy().Boolean() != null) {
							if (constEntry.constEntryDeclLegacy().boolType() != null)
								moduleDef.append(": ").append(constEntry.constEntryDeclLegacy().boolType().getText());
							moduleDef.append(" = ");
							moduleDef.append(constEntry.constEntryDeclLegacy().Boolean().getText());
						} else if (constEntry.constEntryDeclLegacy().Float() != null) {
							if (constEntry.constEntryDeclLegacy().floatType() != null)
								moduleDef.append(": ").append(constEntry.constEntryDeclLegacy().floatType().getText());
							moduleDef.append(" = ");
							moduleDef.append(constEntry.constEntryDeclLegacy().Float().getText());
						} else if (constEntry.constEntryDeclLegacy().Integer() != null) {
							if (constEntry.constEntryDeclLegacy().intType() != null)
								moduleDef.append(": ").append(constEntry.constEntryDeclLegacy().intType().getText());
							moduleDef.append(" = ");
							moduleDef.append(constEntry.constEntryDeclLegacy().Integer().getText());
						} else if (constEntry.constEntryDeclLegacy().String() != null) {
							if (constEntry.constEntryDeclLegacy().stringType() != null)
								moduleDef.append(": ").append(constEntry.constEntryDeclLegacy().stringType().getText());
							moduleDef.append(" = ");
							moduleDef.append(constEntry.constEntryDeclLegacy().String().getText());
						}
						moduleDef.append(";");
					}
					newline(moduleDef);
					indent(moduleDef, 1);
					moduleDef.append("}");
				}
			} else if (moduleElem.methodDefinitionLegacy() != null) {
				newline(moduleDef);
				indent(moduleDef, 1);
				moduleDef.append(methodDefinition(moduleElem.methodDefinitionLegacy()));
			}
			newline(moduleDef);
		}
		moduleDef.append("}");
		return moduleDef.toString();
	}

	private static void generateMeta(org.antlr.v4.runtime.Token doc, ModuleParser.AnnotationsContext anns, StringBuilder builder) {
		generateMeta(doc, anns, builder, 0);
	}

	private static void generateMeta(org.antlr.v4.runtime.Token doc, ModuleParser.AnnotationsContext anns, StringBuilder builder, int indent) {
		if (doc != null) {
			for (String docBit : doc.getText().split("\n")) {
				indent(builder, indent);
				builder.append(docBit.trim());
				newline(builder);
			}
		}
		if (anns != null) {
			for (ModuleParser.AnnotationContext ann : anns.annotation()) {
				indent(builder, indent);
				builder.append(ann.getText());
				newline(builder);
			}
		}
	}

	private static void indent(StringBuilder builder, int depth) {
		while (depth-- > 0) builder.append("\t");
	}

	private static void newline(StringBuilder builder) {
		builder.append("\n");
	}

	private static void space(StringBuilder builder) {
		builder.append(" ");
	}

	private static String methodDefinition(ModuleParser.MethodDefinitionLegacyContext methodDefinition, int indent) {
		String tyParams = methodDefinition.typeParameters() != null
				? typeParameters(methodDefinition.typeParameters())
				: "";
		StringBuilder builder = new StringBuilder();
		generateMeta(methodDefinition.documentation, methodDefinition.annotations(), builder, indent);
		indent(builder, indent);
		builder.append(
				methodDefinition.Name()
		).append(
				tyParams
		).append("(");
		boolean sep = false;
		if (methodDefinition.methodParametersLegacy() != null) {
			for (ModuleParser.MethodParameterLegacyContext methodParameter : methodDefinition.methodParametersLegacy().methodParameterLegacy()) {
				if (sep) builder.append(", ");
				sep = true;
				generateMeta(null, methodParameter.annotations(), builder);
				builder.append(methodParameter.typeNamePairLegacy().Name());
				if (methodParameter.optional != null) builder.append("?");
				builder.append(": ");
				builder.append(complexType(methodParameter.typeNamePairLegacy().complexTypeLegacy()));
			}
		}
		builder.append("): ").append(returnType(methodDefinition.returnTypeLegacy()));
		builder.append(";");
		return builder.toString();
	}

	private static String methodDefinition(ModuleParser.MethodDefinitionLegacyContext methodDefinition) {
		return methodDefinition(methodDefinition, 0);
	}

	private static String propertyDefinition(ModuleParser.PropertyDefinitionLegacyContext propertyDefinition) {
		StringBuilder builder = new StringBuilder();
		generateMeta(propertyDefinition.documentation, propertyDefinition.annotations(), builder, 2);
		indent(builder, 2);
		builder.append(
				propertyDefinition.typeNamePairLegacy().Name()
		);
		if (propertyDefinition.optional != null) builder.append(propertyDefinition.optional.getText());
		builder.append(
				": "
		).append(
				complexType(propertyDefinition.typeNamePairLegacy().complexTypeLegacy())
		).append(
				";"
		);
		return builder.toString();
	}

	private static String returnType(ModuleParser.ReturnTypeLegacyContext returnType) {
		if (returnType.voidType() != null) {
			return "void";
		} else {
			return complexType(returnType.complexTypeLegacy());
		}
	}

	private static String complexType(ModuleParser.ComplexTypeLegacyContext complexType) {
		if (complexType.type() != null) {
			return type(complexType.type());
		} else {
			return typeChain(complexType.typeChain());
		}
	}

	private static String qualifiedName(ModuleParser.QualifiedNameContext qualifiedName) {
		return Joiner.on('.').join(qualifiedName.Name());
	}

	private static String type(ModuleParser.TypeContext type) {
		String dimensions = "";
		if (type.ArrayQualifier() != null) {
			dimensions = Joiner.on("").join(type.ArrayQualifier());
		}
		if (type.primitiveType() != null) {
			return type.primitiveType().getText() + dimensions;
		} else {
			String tyArgs = type.objectTypeLegacy().typeArgumentsLegacy() != null
					? typeArguments(type.objectTypeLegacy().typeArgumentsLegacy())
					: "";
			return qualifiedName(type.objectTypeLegacy().qualifiedName()) + tyArgs + dimensions;
		}
	}

	private static String typeChain(ModuleParser.TypeChainContext typeChain) {
		String dimensionless = null;
		String dimensions = "";
		// Function type
		if (typeChain.typeChainElements().voidType() != null) {
			dimensionless = "()";
		} else {
			dimensionless = "(";
			boolean sep = false;
			for (ModuleParser.TypeChainElementContext typeChainElement : typeChain.typeChainElements().typeChainElement()) {
				if (sep) dimensionless += ", ";
				sep = true;
				dimensionless += typeChainElement(typeChainElement);
			}
			dimensionless += ")";
		}
		dimensionless += " -> " + typeChainReturnType(typeChain.typeChainElements().typeChainReturnType());
		// Determine array dimensions
		if (typeChain.ArrayQualifier() != null && !typeChain.ArrayQualifier().isEmpty()) {
			dimensions = Joiner.on("").join(typeChain.ArrayQualifier());
			dimensionless = "(" + dimensionless + ")";
		}
		return dimensionless + dimensions;
	}

	private static String typeChainReturnType(ModuleParser.TypeChainReturnTypeContext typeChainReturnType) {
		return typeChainReturnType.voidType() != null
				? typeChainReturnType.voidType().getText()
				: typeChainElement(typeChainReturnType.typeChainElement());
	}

	private static String typeChainElement(ModuleParser.TypeChainElementContext typeChainElement) {
		if (typeChainElement.type() != null) {
			return type(typeChainElement.type());
		} else {
			return typeChain(typeChainElement.typeChain());
		}
	}

	private static String typeArguments(ModuleParser.TypeArgumentsLegacyContext typeArgumentsLegacyContext) {
		StringBuilder str = new StringBuilder().append("<");
		boolean sep = false;
		for (ModuleParser.ReturnTypeLegacyContext returnType : typeArgumentsLegacyContext.returnTypeLegacy()) {
			if (sep) str.append(", ");
			sep = true;
			str.append(returnType(returnType));
		}
		return str.append(">").toString();
	}

	private static String typeParameters(ModuleParser.TypeParametersContext typeParametersContext) {
		return "<" + Joiner.on(", ").join(typeParametersContext.Name()) + ">";
	}
}
