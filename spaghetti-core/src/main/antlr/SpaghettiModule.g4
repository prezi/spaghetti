grammar SpaghettiModule;

moduleDefinition	:
	(documentation = Doc)?
	'module' (name = fqName) '{'
		(elements += moduleElement)*
	'}'
	;

moduleElement	: typeDefinition
				| methodDefinition
	;

typeDefinition :
	(documentation = Doc)?
	'interface' (name = Name) ('extends' (superType = fqName))? '{'
		(methods += methodDefinition)*
	'}'
	;

methodDefinition	:
	(documentation = Doc)?
	(returnType = fqName) (arrayDimensions += ArrayQualifier)* (name = Name) '('
		(
			( params += methodParameterDefinition )
			( ',' params += methodParameterDefinition )*
		)?
	')'
	;

methodParameterDefinition :
	(type = fqName) (arrayDimensions += ArrayQualifier)* (name = Name)
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
