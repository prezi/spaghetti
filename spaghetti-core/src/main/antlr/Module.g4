grammar Module;

moduleDefinition : ( documentation = Doc )? annotations?
	'module' qualifiedName
	( 'as' Name )?
	moduleElement*
	;

moduleElement	: importDeclaration
				| typeDefinition
				| externTypeDefinition
				| moduleMethodDefinition
	;

importDeclaration : 'import' qualifiedName ( 'as' Name )?
	;

typeDefinition	: interfaceDefinition
				| structDefinition
				| constDefinition
				| enumDefinition
	;

interfaceDefinition : ( documentation = Doc )? annotations?
	'interface' Name typeParameters?
	( 'extends' superInterfaceDefinition ( ',' superInterfaceDefinition )* )?
	'{'
		interfaceMethodDefinition*
	'}'
	;

superInterfaceDefinition : qualifiedName typeArguments?
	;

externTypeDefinition : ( documentation = Doc )? annotations?
	'extern' 'interface' qualifiedName
    ;

typeParameters : '<' Name ( ',' Name )* '>'
	;

structDefinition : ( documentation = Doc )? annotations?
	'struct' Name typeParameters? '{'
		propertyDefinition*
	'}'
	;

constDefinition : ( documentation = Doc )? annotations?
	'const' Name '{'
		constEntry*
	'}'
	;

constEntry : ( documentation = Doc )? annotations?
	constEntryDecl
	;

constEntryDecl
	: boolType? Name '=' Boolean
	| intType? Name '=' Integer
	| floatType? Name '=' Float
	| stringType? Name '=' String
	;

enumDefinition : ( documentation = Doc )? annotations?
	'enum' Name '{'
		enumValue*
	'}'
	;

enumValue : ( documentation = Doc )? annotations?
 	Name
	;

moduleMethodDefinition : ( documentation = Doc )? annotations?
	(isStatic = 'static')?
	methodDefinition
	;

interfaceMethodDefinition : ( documentation = Doc )? annotations?
	methodDefinition
	;

methodDefinition :
	typeParameters?
	returnType
	Name
	'(' methodParameters? ')'
	;

propertyDefinition : ( documentation = Doc )? annotations?
	( optional = '?' )? typeNamePair
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

methodParameter	: annotations?  ( optional = '?' )? typeNamePair
	;

typeNamePair : complexType Name
	;

returnType
	: voidType
	| complexType
	;

complexType
	: type
	| typeChain
	;

type
	: primitiveType ArrayQualifier*
	| objectType ArrayQualifier*
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
