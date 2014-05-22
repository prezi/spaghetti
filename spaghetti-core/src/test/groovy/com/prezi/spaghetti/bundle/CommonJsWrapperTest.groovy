package com.prezi.spaghetti.bundle

import spock.lang.Specification

/**
 * Created by lptr on 16/05/14.
 */
class CommonJsWrapperTest extends Specification {
	def "CommonJS module"() {
		def originalScript = "__spaghetti(function(__config){});"
		def result = new CommonJsWrapper().wrap("com.example.test", ["com.example.alma", "com.example.bela"], originalScript)

		expect:
		result == [
		        'module.exports=function(){',
					'var __config={',
						'"__baseUrl":__dirname,',
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
							'return __dirname+resource;',
						'}',
					'};',
					'var __spaghetti=function(){',
						'return arguments[0](__config);',
					'};',
					'return ', originalScript,
				'};'
		].join("")
	}

	def "CommonJS application"() {
		def dependencyTree = [
				"com.example.test": ["com.example.alma", "com.example.bela"].toSet(),
				"com.example.alma": ["com.example.bela"].toSet(),
				"com.example.bela": [].toSet()
		]
		def result = new CommonJsWrapper().makeApplication("lajos", "mods", dependencyTree, "com.example.test", true)

		expect:
		result == [
				'var modules=[];',
				'modules.push(require("lajos/mods/com.example.bela")());',
				'modules.push(require("lajos/mods/com.example.alma")(modules[0]));',
				'modules.push(require("lajos/mods/com.example.test")(modules[1],modules[0]));',
				'modules[2]["__module"]["main"]();'
		].join("")
	}

	def "CommonJS cyclic dependency 1"() {
		def dependencyTree = [
				"c": ["a", "b"].toSet(),
				"a": ["b", "c"].toSet(),
				"b": [].toSet()
		]
		when:
		new CommonJsWrapper().makeApplication("lajos", "mods", dependencyTree, "com.example.test", true)

		then:
		thrown IllegalStateException
	}

	def "CommonJS cyclic dependency 2"() {
		def dependencyTree = [
				"c": ["a", "b"].toSet(),
				"a": ["b"].toSet(),
				"b": ["c"].toSet()
		]
		when:
		new CommonJsWrapper().makeApplication("lajos", "mods", dependencyTree, "com.example.test", true)

		then:
		thrown IllegalStateException
	}

	def "CommonJS non-existent module"() {
		def dependencyTree = [
				"c": ["a", "b"].toSet(),
				"a": ["b", "d"].toSet(),
				"b": [].toSet()
		]
		when:
		new CommonJsWrapper().makeApplication("lajos", "mods", dependencyTree, "com.example.test", true)

		then:
		thrown IllegalStateException
	}
}
