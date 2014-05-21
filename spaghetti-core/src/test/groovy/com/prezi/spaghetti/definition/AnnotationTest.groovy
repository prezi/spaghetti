package com.prezi.spaghetti.definition

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lptr on 21/05/14.
 */
class AnnotationTest extends Specification {
	@Unroll
	def "FromContext: #annotationDecl"() {
		def parser = ModuleDefinitionParser.createParser(new ModuleDefinitionSource("test", annotationDecl))
		def annotation = Annotation.fromContext(parser.annotation())

		expect:
		parser.numberOfSyntaxErrors == 0
		annotation.name == name
		annotation.hasDefaultParameter() == defaultParam != null
		if (defaultParam) {
			annotation.getDefaultParameter() == defaultParam
		}
		annotation.parameters.sort() == params.sort()
		annotation.parameters.collectEntries { name, value -> [name, value?.class] }.sort() == types.sort()

		where:
		annotationDecl                        | name    | defaultParam | params                     | types
		'@alma'                               | "alma"  | null         | [:]                        | [:]
		'@alma()'                             | "alma"  | null         | [:]                        | [:]
		'@bela(null)'                         | "bela"  | null         | [default: null]            | [default: null]
		'@bela("tibor")'                      | "bela"  | "tibor"      | [default: "tibor"]         | [default: String]
		'@tibor(n = null)'                    | "tibor" | null         | [n: null]                  | [n: null]
		'@tibor(n = 0x123)'                   | "tibor" | null         | [n: 0x123]                 | [n: Integer]
		'@tibor(n = -0x123)'                  | "tibor" | null         | [n: -0x123]                | [n: Integer]
		'@tibor(n = 1)'                       | "tibor" | null         | [n: 1]                     | [n: Integer]
		'@tibor(n = -5)'                      | "tibor" | null         | [n: -5]                    | [n: Integer]
		'@tibor(n = -1.2)'                    | "tibor" | null         | [n: -1.2]                  | [n: Double]
		'@tibor(n = "geza\\"tibor")'          | "tibor" | null         | [n: 'geza"tibor']          | [n: String]
		'@geza(n = null, g = "geza\\"tibor")' | "geza"  | null         | [n: null, g: 'geza"tibor'] | [n: null, g: String]
	}
}
