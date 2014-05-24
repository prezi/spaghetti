grammar Module;

moduleDefinition : (documentation = Doc)? annotations?
	'module' (name = qualifiedName)
	('as' (alias = Name))?
	moduleElement*
	;

moduleElement	: importDeclaration
				| typeDefinition
				| externTypeDefinition
				| moduleMethodDefinition
	;

importDeclaration : 'import' (name = qualifiedName) ('as' (alias = Name))?
	;

typeDefinition	: interfaceDefinition
				| structDefinition
				| constDefinition
				| enumDefinition
	;

interfaceDefinition : (documentation = Doc)? annotations?
	'interface' (name = Name) typeParameters?
	( 'extends' superInterfaceDefinition (',' superInterfaceDefinition )* )?
	'{'
		interfaceMethodDefinition*
	'}'
	;

superInterfaceDefinition : qualifiedName typeArguments?
	;

externTypeDefinition : (documentation = Doc)? annotations?
	'extern' 'interface' (name = qualifiedName)
    ;

typeParameters : '<' (parameters += typeParameter ) ( ',' ( parameters += typeParameter ) )* '>'
	;

typeParameter : (name = Name)
	;

structDefinition : (documentation = Doc)? annotations?
	'struct' (name = Name) '{'
		propertyDefinition*
	'}'
	;

constDefinition : (documentation = Doc)? annotations?
	'const' ( name = Name ) '{'
		constEntry*
	'}'
	;

constEntry : (documentation = Doc)? annotations?
	constEntryDecl
	;

constEntryDecl
	: boolType? ( name = Name ) '=' ( boolValue = Boolean )
	| intType? ( name = Name ) '=' ( intValue = Integer )
	| floatType? ( name = Name ) '=' ( floatValue = Float )
	| stringType? ( name = Name ) '=' ( stringValue = String )
	;

enumDefinition : (documentation = Doc)? annotations?
	'enum' (name = Name) '{'
		(values += enumValue)*
	'}'
	;

enumValue : (documentation = Doc)? annotations?
 	(name = Name)
	;

moduleMethodDefinition : (documentation = Doc)? annotations?
	(isStatic = 'static')?
	methodDefinition
	;

interfaceMethodDefinition : (documentation = Doc)? annotations?
	methodDefinition
	;

methodDefinition :
	typeParameters?
	returnTypeChain
	(name = Name)
	'(' ( parameters = typeNamePairs )? ')'
	;

propertyDefinition : (documentation = Doc)? annotations?
	(property = typeNamePair)
	;

annotations : annotation+
	;

annotation : '@' (name = Name) ( '(' annotationParameters? ')' )?
	;

annotationParameters	: annotationValue
						| annotationParameter ( ',' annotationParameter )*
	;

annotationParameter : ( name = Name ) '=' annotationValue
	;

annotationValue	: ( nullValue = Null )		# annotationNullParameter
				| ( boolValue = Boolean )	# annotationBooleanParameter
				| ( intValue = Integer )	# annotationIntParameter
				| ( floatValue = Float )	# annotationFloatParameter
 				| ( stringValue = String )	# annotationStringParameter
	;

typeNamePairs : ( elements += typeNamePair ) ( ',' elements += typeNamePair )*
	;

typeNamePair : annotations? (type = typeChain) (name = Name)
	;

returnTypeChain	: voidType		# voidReturnTypeChain
				| typeChain		# normalReturnTypeChain
	;

typeChain	: valueType
			| callbackTypeChain
	;

callbackTypeChain	: ( elements += typeChainElement ) ( '->' ( elements += typeChainElement)  )+
	;

typeChainElement	: returnType		# simpleTypeChainElement
					| '(' typeChain ')'	# subTypeChainElement
	;

returnType	: voidType
			| valueType
	;

valueType	: primitiveType ArrayQualifier*
			| moduleType ArrayQualifier*
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

moduleType : ( name = qualifiedName ) ( arguments = typeArguments )?
	;

typeArguments : '<' ( arguments += returnType ) ( ',' ( arguments += returnType ) )* '>'
	;

qualifiedName : ( parts += Name ) ( '.' parts += Name )*
	;

Null				: 'null';
Boolean				: ( 'true' | 'false' );
Integer				: SIGN? INTEGER_NUMBER;
Float				: SIGN? NON_INTEGER_NUMBER;
String				: '"' STRING_GUTS '"';
Doc					: '/**' .*? '*/' '\r'* '\n'?;
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
