grammar SpaghettiModule;

moduleDefinition	:
	(doc = DOC)?
	'module' fqName = FQID '{'
		(elements += moduleElement)*
	'}'
	;

moduleElement	: typeDefinition
				| methodDefinition
	;

docComment		: doc = DOC
	;

typeDefinition :
	docComment?
	'type' name = ID '{'
		(methods += methodDefinition)*
	'}'
	;

methodDefinition	:
	docComment?
	returnType = ID name = ID '('
		(
			params += methodParameterDefinition
			( ',' params += methodParameterDefinition )*
		)?
	')'
	;

methodParameterDefinition :
	type = ID
	name = ID
	;

ID		: [a-zA-Z][a-zA-Z0-9]*;
FQID	: (ID '.')* ID;
DOC		: '/**' .*? '*/';
BLOCK_COMMENT	: '/*' (.*?) '*/' -> skip;
LINE_COMMENT	: '//' .*? '\n' -> skip;
WS		: [ \t\r\n]+ -> skip ; // Define whitespace rule, toss it out
