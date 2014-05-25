package com.prezi.spaghetti.packaging

import spock.lang.Specification

/**
 * Created by lptr on 16/05/14.
 */
class SingleFileWrapperTest extends Specification {
	def "Single file module"() {
		def originalScript = "__spaghetti(function(SpaghettiConfiguration){});"
		def result = new SingleFileWrapper().wrap("com.example.test", ["com.example.alma", "com.example.bela"], originalScript)

		expect:
		result == [
		        'function(){',
					'var SpaghettiConfiguration={',
						'"__baseUrl":__dirname+"/com.example.test",',
						'"__modules":{',
							'"com.example.alma":arguments[0],',
							'"com.example.bela":arguments[1]',
						'},',
						'getName:function(){',
							'return "com.example.test";',
						'},',
						'getResourceUrl:function(resource){',
							'if(resource.substr(0,1)!="/"){',
								'resource="/"+resource;',
							'}',
							'return __dirname+"/com.example.test"+resource;',
						'}',
					'};',
					'var __spaghetti=function(){',
						'return arguments[0](SpaghettiConfiguration);',
					'};',
					'return ', originalScript,
				'}'
		].join("")
	}

	def "Single file application"() {
		def dependencyTree = [
				"com.example.test": ["com.example.alma", "com.example.bela"].toSet(),
				"com.example.alma": ["com.example.bela"].toSet(),
				"com.example.bela": [].toSet()
		]
		def result = new SingleFileWrapper().makeApplication("lajos", "mods", dependencyTree, "com.example.test", true)

		expect:
		result == [
				'modules["com.example.test"]["__instance"]["main"]();'
		].join("")
	}
}
