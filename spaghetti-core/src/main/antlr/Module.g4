grammar Module;

moduleDefinitionLegacy : ( documentation = Doc )? annotations?
	'module' qualifiedName
	( 'as' Name )?
	moduleElementLegacy*
	;

moduleDefinition : ( documentation = Doc )? annotations?
	'module' qualifiedName
	( 'as' Name )?
	'{'
	moduleElement*
	'}'
	;

moduleElement	: importDeclaration ';'
				| typeDefinition
				| externTypeDefinition
				| methodDefinition ';'
	;

moduleElementLegacy : importDeclaration
					| typeDefinitionLegacy
					| externTypeDefinitionLegacy
					| methodDefinitionLegacy
	;

importDeclaration : 'import' qualifiedName ( 'as' Name )?
	;

typeDefinition	: interfaceDefinition
				| structDefinition
				| constDefinition
				| enumDefinition
	;

typeDefinitionLegacy	: interfaceDefinitionLegacy
						| structDefinitionLegacy
						| constDefinitionLegacy
						| enumDefinitionLegacy
	;

externTypeDefinition
	: externInterfaceDefinition
	;

externTypeDefinitionLegacy
	: externInterfaceDefinitionLegacy
	;

interfaceDefinition : ( documentation = Doc )? annotations?
	'interface' Name typeParameters?
	( 'extends' superTypeDefinition ( ',' superTypeDefinition )* )?
	'{'
		( methodDefinition ';' )*
	'}'
	;

interfaceDefinitionLegacy : ( documentation = Doc )? annotations?
	'interface' Name typeParameters?
	( 'extends' superTypeDefinitionLegacy ( ',' superTypeDefinitionLegacy )* )?
	'{'
		methodDefinitionLegacy*
	'}'
	;

superTypeDefinition : qualifiedName typeArguments?
	;

superTypeDefinitionLegacy : qualifiedName typeArgumentsLegacy?
	;

externInterfaceDefinition : ( documentation = Doc )? annotations?
	'extern' 'interface' qualifiedName typeParameters? ';'
    ;

externInterfaceDefinitionLegacy : ( documentation = Doc )? annotations?
	'extern' 'interface' qualifiedName typeParameters?
    ;

typeParameters : '<' Name ( ',' Name )* '>'
	;

structDefinition : ( documentation = Doc )? annotations?
	'struct' Name typeParameters?
	( 'extends' superTypeDefinition )?
	'{'
		( structElementDefinition ';' )*
	'}'
	;

structDefinitionLegacy : ( documentation = Doc )? annotations?
	'struct' Name typeParameters?
	( 'extends' superTypeDefinitionLegacy )?
	'{'
		structElementDefinitionLegacy*
	'}'
	;

structElementDefinition
	: propertyDefinition
	| methodDefinition
	;

structElementDefinitionLegacy
	: propertyDefinitionLegacy
	| methodDefinitionLegacy
	;

constDefinition : ( documentation = Doc )? annotations?
	'const' Name '{'
		( constEntry  ';' )*
	'}'
	;

constDefinitionLegacy : ( documentation = Doc )? annotations?
	'const' Name '{'
		( constEntryLegacy )*
	'}'
	;

constEntry : ( documentation = Doc )? annotations?
	constEntryDecl
	;

constEntryLegacy : ( documentation = Doc )? annotations?
	constEntryDeclLegacy
	;

constEntryDecl
	: Name ( ':' boolType )? '=' Boolean
	| Name ( ':' intType)? '=' Integer
	| Name ( ':' floatType)? '=' Float
	| Name ( ':' stringType)? '=' String
	;

constEntryDeclLegacy
	: boolType? Name '=' Boolean
	| intType? Name '=' Integer
	| floatType? Name '=' Float
	| stringType? Name '=' String
	;

enumDefinition : ( documentation = Doc )? annotations?
	'enum' Name '{'
		( enumValue ( ',' enumValue )* )?
	'}'
	;

enumDefinitionLegacy : ( documentation = Doc )? annotations?
	'enum' Name '{'
		( enumValue )*
	'}'
	;

enumValue : ( documentation = Doc )? annotations?
 	Name ('=' value = Integer)?
	;

methodDefinition : ( documentation = Doc )? annotations?
	Name
	typeParameters?
	'(' methodParameters? ')'
	':'
	returnType
	;

methodDefinitionLegacy : ( documentation = Doc )? annotations?
	typeParameters?
	returnTypeLegacy
	Name
	'(' methodParametersLegacy? ')'
	;

propertyDefinition : ( documentation = Doc )? annotations?
	typeNamePair
	;

propertyDefinitionLegacy : ( documentation = Doc )? annotations?
	( optional = '?' )? typeNamePairLegacy
	;

annotations : annotation+
	;

annotation : '@' Name ( '(' annotationParameters? ')' )?
	;

annotationParameters	: annotationValue
						| annotationParameter ( ',' annotationParameter )*
	;

annotationParameter : Name '=' annotationValue
	;

annotationValue	: Null
				| Boolean
				| Integer
				| Float
 				| String
	;

methodParameters : methodParameter ( ',' methodParameter )*
	;

methodParametersLegacy : methodParameterLegacy ( ',' methodParameterLegacy )*
	;

methodParameter	: annotations? typeNamePair
	;

methodParameterLegacy	: annotations?  ( optional = '?' )? typeNamePairLegacy
	;

typeNamePair : Name ( optional = '?' )? ':' complexType
	;

typeNamePairLegacy : complexTypeLegacy Name
	;

complexType
	: primitiveType ArrayQualifier*
	| objectType ArrayQualifier*
	| functionType
	| '(' functionType ')' ArrayQualifier*
	;

complexTypeLegacy
	: type
	| typeChain
	;

type
	: primitiveType ArrayQualifier*
	| objectTypeLegacy ArrayQualifier*
	;

typeChain
	: typeChainElements
	| '(' typeChainElements ')' ArrayQualifier+
	;

typeChainElements
	: voidType '->' typeChainReturnType
	| typeChainElement ( '->' typeChainElement )* '->' typeChainReturnType
	;

typeChainReturnType
	: voidType
	| typeChainElement
	;

typeChainElement
	: type
	| '(' typeChain ')'
	;

functionType
	: '(' functionParameters? ')' '->' returnType
	;

functionParameters
	: complexType ( ',' complexType )*
	;

returnType
	: voidType
	| complexType
	;

returnTypeLegacy
	: voidType
	| complexTypeLegacy
	;

primitiveType	: boolType
				| intType
				| floatType
				| stringType
				| anyType
	;

boolType : 'bool'
	;

intType : 'int'
	;

floatType : 'float'
	;

stringType : 'string'
	;

anyType : 'any'
	;

voidType : 'void'
	;

objectType : qualifiedName typeArguments?
	;

objectTypeLegacy : qualifiedName typeArgumentsLegacy?
	;

typeArguments : '<' returnType ( ',' returnType )* '>'
	;

typeArgumentsLegacy : '<' returnTypeLegacy ( ',' returnTypeLegacy )* '>'
	;

qualifiedName : Name ( '.' Name )*
	;

Null				: 'null';
Boolean				: ( 'true' | 'false' );
Integer				: SIGN? INTEGER_NUMBER;
Float				: SIGN? NON_INTEGER_NUMBER;
String				: '"' STRING_GUTS '"';
Doc					: '/**' .*? '*/';
Name				: [_a-zA-Z][_a-zA-Z0-9]*;
ArrayQualifier		: '[' ']';

BlockComment		: '/*' (.*?) '*/' -> skip;
LineComment			: '//' .*? '\n' -> skip;
WhiteSpace			: [ \t\r\n]+ -> skip;


fragment STRING_GUTS : (ESC | ~('\\' | '"'))*;
fragment ESC :  '\\' ('\\' | '"');

fragment INTEGER_NUMBER
	:	'0'
// Let's not support octal for now
//	|	'0' ('0'..'7')+
	|	'1'..'9' DIGIT*
	|	HEX_PREFIX HEX_DIGIT+
	;

fragment NON_INTEGER_NUMBER
	:	DIGIT+ '.' DIGIT* EXPONENT?
	|	'.' DIGIT+ EXPONENT?
	|	DIGIT+ EXPONENT
	|	DIGIT+
// Let's not support hex floats
//	|
//		HEX_PREFIX (HEX_DIGIT )*
//		(	()
//		|	('.' (HEX_DIGIT )* )
//		)
//		( 'p' | 'P' )
//		SIGN?
//		DIGIT+
		;

fragment EXPONENT
	:	( 'e' | 'E' ) SIGN? DIGIT+
	;

fragment SIGN
	:	( '+' | '-' )
	;

fragment DIGIT
	: ( '0' .. '9' )
	;

fragment HEX_PREFIX
	:	'0x' | '0X'
	;

fragment HEX_DIGIT
	:	( '0'..'9' | 'a'..'f' | 'A'..'F' )
	;
