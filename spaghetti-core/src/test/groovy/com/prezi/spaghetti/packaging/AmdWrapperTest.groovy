package com.prezi.spaghetti.packaging

import spock.lang.Specification

/**
 * Created by lptr on 16/05/14.
 */
class AmdWrapperTest extends Specification {
	def "AMD module"() {
		def originalScript = "__spaghetti(function(SpaghettiConfiguration){});"
		def result = new AmdWrapper().wrap("com.example.test", ["com.example.alma", "com.example.bela"], originalScript)

		expect:
		result == [
		        'define(["require","com.example.alma","com.example.bela"],function(){',
					'var moduleUrl=arguments[0]["toUrl"]("com.example.test.js");',
					'var baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf("/")+1);',
					'var SpaghettiConfiguration={',
						'"__baseUrl":baseUrl,',
						'"__modules":{',
							'"require":arguments[0],',
							'"com.example.alma":arguments[1],',
							'"com.example.bela":arguments[2]',
						'},',
						'getName:function(){',
							'return "com.example.test";',
						'},',
						'getResourceUrl:function(resource){',
							'if(resource.substr(0,1)=="/"){',
								'resource=resource.substr(1);',
							'}',
							'return baseUrl+resource;',
						'}',
					'};',
					'var __spaghetti=function(){',
						'return arguments[0](SpaghettiConfiguration);',
					'};',
					'return ', originalScript,
				'});'
		].join("")
	}

	def "AMD application"() {
		def dependencyTree = [
				"com.example.test": ["com.example.alma", "com.example.bela"].toSet(),
				"com.example.alma": ["com.example.bela"].toSet(),
				"com.example.bela": [].toSet()
		]
		def result = new AmdWrapper().makeApplication("lajos", "mods", dependencyTree, "com.example.test", true)

		expect:
		result == [
				'require["config"]({',
					'"baseUrl":"lajos",',
					'"paths":{',
						'"com.example.alma": "mods/com.example.alma/com.example.alma",',
						'"com.example.bela": "mods/com.example.bela/com.example.bela",',
						'"com.example.test": "mods/com.example.test/com.example.test"',
					'}',
				'});',
		        'require(["com.example.test"],function(__mainModule){',
					'__mainModule["__instance"]["main"]();',
				'});'
		].join("")
	}
}
