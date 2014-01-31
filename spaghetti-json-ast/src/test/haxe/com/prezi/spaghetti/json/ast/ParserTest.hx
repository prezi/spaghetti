package com.prezi.spaghetti.json.ast;

class ParserTest {
	@Test
	public function testParser() {
		Parser.parse('
{
	"name": "prezi.test.Test",
	"consts": [
		{
			"name": "Values",
			"values": [
				{
					"name": "HELLO",
					"type": "int"
				},
				{
					"name": "HI",
					"type": "string"
				},
				{
					"name": "defaultStyle",
					"type": "prezi.test.CharacterStyle"
				}
			]
		}
	],
	"enums": [
		{
			"name": "CharacterStyleType",
			"names": [
				{
					"name": "COLOR"
				},
				{
					"name": "FONT_FAMILY"
				}
			]
		}
	],
	"interfaces": [
		{
			"name": "AbstractText",
			"methods": [
				{
					"name": "insert",
					"parameters": [
						{
							"name": "offset",
							"type": "int"
						},
						{
							"name": "text",
							"type": "UnicodeString"
						},
						{
							"name": "withStyles",
							"annotations": [
								{
									"name": "nullable"
								}
							],
							"type": {
								"array": "prezi.test.CharacterStyle"
							}
						}
					],
					"returnType": "void"
				},
				{
					"name": "delete",
					"parameters": [
						{
							"name": "offset",
							"type": "int"
						},
						{
							"name": "end",
							"type": "int"
						}
					],
					"returnType": "void"
				}
			]
		},
		{
			"name": "Text",
			"methods": [
				{
					"name": "getRawText",
					"parameters": [

					],
					"returnType": "UnicodeString"
				}
			]
		},
		{
			"name": "TestStuff",
			"parameters": [
				"Pre",
				"Post"
			],
			"methods": [
				{
					"name": "registerCallback",
					"parameters": [
						{
							"name": "callback",
							"type": {
								"chain": [
									"string",
									"void"
								]
							}
						}
					],
					"returnType": "void"
				},
				{
					"name": "doVoidCallback",
					"parameters": [
						{
							"name": "callback",
							"type": {
								"chain": [
									"void",
									"int"
								]
							}
						}
					],
					"returnType": "int"
				},
				{
					"name": "doAsync",
					"parameters": [
						{
							"name": "callback",
							"type": {
								"chain": [
									"string",
									{
										"chain": [
											"int",
											"string"
										]
									},
									"void"
								]
							}
						},
						{
							"name": "converter",
							"type": {
								"chain": [
									"int",
									"string"
								]
							}
						}
					],
					"returnType": "int"
				},
				{
					"name": "doSomething",
					"parameters": [
						{
							"name": "pre",
							"type": "Pre"
						},
						{
							"name": "text",
							"type": "string"
						},
						{
							"name": "post",
							"type": "Post"
						}
					],
					"returnType": "string"
				},
				{
					"name": "drawSomething",
					"parameters": [
						{
							"name": "canvas",
							"type": "HTMLCanvasElement"
						}
					],
					"returnType": "void"
				}
			]
		},
		{
			"name": "Dummy",
			"methods": [
				{
					"name": "getValue",
					"parameters": [

					],
					"returnType": "int"
				}
			]
		},
		{
			"name": "TestStuff2",
			"parameters": [
				"T"
			],
			"methods": [
				{
					"name": "readValue",
					"parameters": [
						{
							"name": "object",
							"type": "T"
						},
						{
							"name": "name",
							"type": "string"
						},
						{
							"name": "defaultValue",
							"type": "X"
						}
					],
					"returnType": "X"
				}
			]
		}
	],
	"structs": [
		{
			"name": "CharacterStyle",
			"annotations": [
				{
					"name": "annotated",
					"parameters": {
						"name": "meta",
						"param": true
					}
				}
			],
			"properties": [
				{
					"name": "type",
					"type": "prezi.test.CharacterStyleType"
				},
				{
					"name": "value",
					"type": "any"
				}
			]
		}
	],
	"methods": [
		{
			"name": "createText",
			"parameters": [

			],
			"returnType": "prezi.test.Text"
		},
		{
			"name": "createTestStuff",
			"parameters": [

			],
			"returnType": "prezi.test.TestStuff"
		}
	]
}
		');
	}
}