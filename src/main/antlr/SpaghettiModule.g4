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
	'type' (name = Name) '{'
		(methods += methodDefinition)*
	'}'
	;

methodDefinition	:
	(documentation = Doc)?
	(returnType = fqName) (name = Name) '('
		(
			( params += methodParameterDefinition )
			( ',' params += methodParameterDefinition )*
		)?
	')'
	;

methodParameterDefinition :
	(type = fqName) (name = Name)
	;

fqName	: qualifiedName = QualifiedName
		| name = Name
	;

fragment NAME		: [a-zA-Z][a-zA-Z0-9]*;

QualifiedName		: (NAME '.')+ NAME;
Name				: NAME;
Doc					: '/**' .*? '*/' '\r'* '\n'?;

BlockComment		: '/*' (.*?) '*/' -> skip;
LineComment			: '//' .*? '\n' -> skip;
WhiteSpace			: [ \t\r\n]+ -> skip;
