grammar SpaghettiModule;

moduleDefinition :
	(documentation = Doc)?
	'module' (name = qualifiedName) '{'
		moduleElement*
	'}'
	;

moduleElement	: typeDefinition
				| enumDefinition
				| typeElement
	;

typeDefinition :
	(documentation = Doc)?
	'interface' (name = Name) ('extends' (superType = qualifiedName))? '{'
		typeElement*
	'}'
	;

enumDefinition :
	(documentation = Doc)?
	'enum' (name = Name) '{'
		(values += enumValue)*
	'}'
	;

enumValue : (documentation = Doc)? (name = Name)
	;

typeElement	: methodDefinition
			| propertyDefinition
	;

methodDefinition :
	(documentation = Doc)?
	(returnType = valueType) (name = Name) '('
		(
			( params += typedName )
			( ',' params += typedName )*
		)?
	')'
	;

propertyDefinition :
	(documentation = Doc)?
    (property = typedName)
	;

typedName : (type = valueType) (name = Name)
	;

valueType : (name = qualifiedName) (arrayDimensions += ArrayQualifier)*
	;

qualifiedName : ( parts += Name ) ( '.' parts += Name )*
	;

Name				: [_a-zA-Z][_a-zA-Z0-9]*;
Doc					: '/**' .*? '*/' '\r'* '\n'?;
ArrayQualifier		: '[' ']';

BlockComment		: '/*' (.*?) '*/' -> skip;
LineComment			: '//' .*? '\n' -> skip;
WhiteSpace			: [ \t\r\n]+ -> skip;
