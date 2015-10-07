grammar Module;

moduleDefinition : ( documentation = Doc )? annotations?
	'module' qualifiedName
	( 'as' Name )?
	'{'
	moduleElement*
	'}'
	;

moduleElement	: importDeclaration
				| typeDefinition
				| externTypeDefinition
				| methodDefinition ';'
	;

importDeclaration : 'import' qualifiedName ( 'as' Name )? ';'
	;

typeDefinition	: interfaceDefinition
				| structDefinition
				| constDefinition
				| enumDefinition
	;

externTypeDefinition
	: externInterfaceDefinition
	;

interfaceDefinition : ( documentation = Doc )? annotations?
	'interface' Name typeParameters?
	( 'extends' superTypeDefinition ( ',' superTypeDefinition )* )?
	'{'
		( methodDefinition ';' )*
	'}'
	;

superTypeDefinition : qualifiedName typeArguments?
	;

externInterfaceDefinition : ( documentation = Doc )? annotations?
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

structElementDefinition
	: propertyDefinition
	| methodDefinition
	;

constDefinition : ( documentation = Doc )? annotations?
	'const' Name '{'
		( constEntry  ';' )*
	'}'
	;

constEntry : ( documentation = Doc )? annotations?
	constEntryDecl
	;

constEntryDecl
	: Name ( ':' boolType )? '=' Boolean
	| Name ( ':' intType)? '=' Integer
	| Name ( ':' floatType)? '=' Float
	| Name ( ':' stringType)? '=' String
	;

enumDefinition : ( documentation = Doc )? annotations?
	'enum' Name '{'
		( enumValue ( ',' enumValue )* )?
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

propertyDefinition : ( documentation = Doc )? annotations?
	typeNamePair
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

methodParameter	: annotations? typeNamePair
	;

typeNamePair : Name ( optional = '?' )? ':' complexType
	;

complexType
	: primitiveType ArrayQualifier*
	| objectType ArrayQualifier*
	| functionType
	| '(' functionType ')' ArrayQualifier*
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

typeArguments : '<' returnType ( ',' returnType )* '>'
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
