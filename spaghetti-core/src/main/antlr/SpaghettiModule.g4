grammar SpaghettiModule;

moduleDefinition :
	(documentation = Doc)?
	'module' (name = fqName) '{'
		moduleElement*
	'}'
	;

moduleElement	: typeDefinition
				| enumDefinition
				| typeElement
	;

typeDefinition :
	(documentation = Doc)?
	'interface' (name = Name) ('extends' (superType = fqName))? '{'
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

valueType : (name = fqName) (arrayDimensions += ArrayQualifier)*
	;

fqName	: qualifiedName = QualifiedName
		| name = Name
	;

fragment NAME		: [_a-zA-Z][_a-zA-Z0-9]*;

QualifiedName		: (NAME '.')+ NAME;
Name				: NAME;
Doc					: '/**' .*? '*/' '\r'* '\n'?;
ArrayQualifier		: '[' ']';

BlockComment		: '/*' (.*?) '*/' -> skip;
LineComment			: '//' .*? '\n' -> skip;
WhiteSpace			: [ \t\r\n]+ -> skip;
