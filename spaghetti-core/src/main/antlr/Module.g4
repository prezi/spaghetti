grammar Module;

moduleDefinition : (documentation = Doc)?
	'module' (name = qualifiedName) '{'
		moduleElement*
	'}'
	;

moduleElement	: typeDefinition
				| enumDefinition
				| typeElement
	;

typeDefinition : (documentation = Doc)?
	'interface' (name = Name) ('extends' (superType = qualifiedName))? '{'
		typeElement*
	'}'
	;

enumDefinition : (documentation = Doc)?
	'enum' (name = Name) '{'
		(values += enumValue)*
	'}'
	;

enumValue : (documentation = Doc)?
 	(name = Name)
	;

typeElement	: methodDefinition
			| propertyDefinition
	;

methodDefinition : (documentation = Doc)?
	returnType (name = Name) '(' ( parameters = typedNameList )? ')'
	;

propertyDefinition : (documentation = Doc)?
	(property = typedName)
	;

typedNameList : ( elements += typedName ) ( ',' elements += typedName )*
	;

typedName : (type = valueType) (name = Name)
	;

returnType	: 'void'		# voidReturnType
			| valueType		# normalReturnType
	;

valueType	: primitiveType ArrayQualifier*
			| moduleType ArrayQualifier*
	;

primitiveType	: 'bool'
				| 'int'
				| 'float'
				| 'String'
				| 'any'
	;

moduleType : ( name = qualifiedName )
	;

qualifiedName : ( parts += Name ) ( '.' parts += Name )*
	;

Name				: [_a-zA-Z][_a-zA-Z0-9]*;
Doc					: '/**' .*? '*/' '\r'* '\n'?;
ArrayQualifier		: '[' ']';

BlockComment		: '/*' (.*?) '*/' -> skip;
LineComment			: '//' .*? '\n' -> skip;
WhiteSpace			: [ \t\r\n]+ -> skip;
