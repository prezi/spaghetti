grammar Module;

moduleDefinition : (documentation = Doc)?
	'module' (name = qualifiedName) '{'
		moduleElement*
	'}'
	;

moduleElement	: typeDefinition
				| structDefinition
				| enumDefinition
				| methodDefinition
	;

typeDefinition : (documentation = Doc)? annotations?
	'interface' (name = Name) typeParameters? ('extends' (superType = qualifiedName))? '{'
		typeElement*
	'}'
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

enumDefinition : (documentation = Doc)? annotations?
	'enum' (name = Name) '{'
		(values += enumValue)*
	'}'
	;

enumValue : (documentation = Doc)? annotations?
 	(name = Name)
	;

typeElement	: methodDefinition
			| propertyDefinition
	;

methodDefinition : (documentation = Doc)? annotations?
	returnTypeChain (name = Name) '(' ( parameters = typeNamePairs )? ')'
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

annotationValue	: ( nullValue = 'null' )				# annotationNullParameter
				| ( boolValue = ( 'true' | 'false' ) )	# annotationBooleanParameter
				| ( numberValue = Number )				# annotationNumberParameter
 				| ( stringValue = '"' .*? '"' )			# annotationStringParameter
	;

typeNamePairs : ( elements += typeNamePair ) ( ',' elements += typeNamePair )*
	;

typeNamePair : annotations? (type = typeChain) (name = Name)
	;

returnTypeChain	: voidType		# voidReturnTypeChain
				| typeChain		# normalReturnTypeChain
	;

typeChain	: valueType							# normalValueTypeChain
			| returnType ( '->' returnType )+	# callbackTypeChain
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
				| 'String'
				| 'any'
	;

moduleType : ( name = qualifiedName ) ( arguments = typeArguments )?
	;

typeArguments : '<' ( arguments += returnType ) ( ',' ( arguments += returnType ) )* '>'
	;

qualifiedName : ( parts += Name ) ( '.' parts += Name )*
	;

Name				: [_a-zA-Z][_a-zA-Z0-9]*;
Number				: [0-9]+ ( '.' [0-9]+ )?;
Doc					: '/**' .*? '*/' '\r'* '\n'?;
ArrayQualifier		: '[' ']';

BlockComment		: '/*' (.*?) '*/' -> skip;
LineComment			: '//' .*? '\n' -> skip;
WhiteSpace			: [ \t\r\n]+ -> skip;
