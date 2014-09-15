package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.internal.Version
import com.prezi.spaghetti.packaging.internal.CommonJsModuleWrapper

class CommonJsModuleWrapperTest extends WrapperTestBase {
	def "CommonJS module"() {
		def originalScript = "/* Generated by Spaghetti */ module(function(Spaghetti){})"
		def result = new CommonJsModuleWrapper().wrap(mockParams("com.example.test", "1.0", ["com.example.alma", "com.example.bela"], originalScript))

		expect:
		result == [
		        'module.exports=(function(){',
					'var module=(function(dependencies){',
						'return function(init){',
							'return init.call({},(function(){',
								'var baseUrl=__dirname;',
								'return{',
									'getSpaghettiVersion:function(){return "' + Version.SPAGHETTI_VERSION + '";},',
									'getName:function(){',
										'return "com.example.test";',
									'},',
									'getVersion:function(){return "1.0";},',
									'getResourceUrl:function(resource){',
										'if(resource.substr(0,1)!="/"){',
											'resource="/"+resource;',
										'}',
										'return baseUrl+resource;',
									'},',
									'"dependencies":{',
										'"com.example.alma":require("com.example.alma"),',
										'"com.example.bela":require("com.example.bela")',
									'}',
								'};',
							'})());',
						'};',
					'})(arguments);',
					'/* Generated by Spaghetti */ ',
					'return{',
						'"module":module(function(Spaghetti){}),',
						'"version":"1.0",',
						'"spaghettiVersion":"' + Version.SPAGHETTI_VERSION + '"',
					'};',
				'})();'
		].join("")
	}

	def "CommonJS application"() {
		def dependencyTree = [
				"com.example.test": ["com.example.alma", "com.example.bela"].toSet(),
				"com.example.alma": ["com.example.bela"].toSet(),
				"com.example.bela": [].toSet()
		]
		def result = new CommonJsModuleWrapper().makeApplication(dependencyTree, "com.example.test", true)

		expect:
		result == [
				'var mainModule=require("com.example.test")["module"];',
				'mainModule["main"]();\n',
		].join("")
	}
}
