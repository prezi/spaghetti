grammar Module;

moduleDefinition : (documentation = Doc)?
	'module' (name = qualifiedName)
	moduleElement*
	;

moduleElement	: importDeclaration
				| typeDefinition
				| externTypeDefinition
				| methodDefinition
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
		methodDefinition*
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
		propertyDefinition*
	'}'
	;

enumDefinition : (documentation = Doc)? annotations?
	'enum' (name = Name) '{'
		(values += enumValue)*
	'}'
	;

enumValue : (documentation = Doc)? annotations?
 	(name = Name)
	;

methodDefinition : (documentation = Doc)? annotations?
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

annotationParameters	: ( singleValue = annotationValue )
						| annotationParameter ( ',' annotationParameter )*
	;

annotationParameter : ( name = Name ) '=' annotationValue
	;

annotationValue	: ( nullValue = Null )		# annotationNullParameter
				| ( boolValue = Bool )		# annotationBooleanParameter
				| ( numberValue = Number )	# annotationNumberParameter
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

voidType : 'void'
	;

primitiveType	: 'bool'
				| 'int'
				| 'float'
				| 'string'
				| 'any'
	;

moduleType : ( name = qualifiedName ) ( arguments = typeArguments )?
	;

typeArguments : '<' ( arguments += returnType ) ( ',' ( arguments += returnType ) )* '>'
	;

qualifiedName : ( parts += Name ) ( '.' parts += Name )*
	;

Name				: [_a-zA-Z][_a-zA-Z0-9]*;
Null				: 'null';
Bool				: ( 'true' | 'false' );
Number				: [0-9]+ ( '.' [0-9]+ )?;
String				: '"' STRING_GUTS '"';
Doc					: '/**' .*? '*/' '\r'* '\n'?;
ArrayQualifier		: '[' ']';

BlockComment		: '/*' (.*?) '*/' -> skip;
LineComment			: '//' .*? '\n' -> skip;
WhiteSpace			: [ \t\r\n]+ -> skip;

fragment STRING_GUTS : (ESC | ~('\\' | '"'))*;
fragment ESC :  '\\' ('\\' | '"');
