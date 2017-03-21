package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.generator.internal.InternalGeneratorUtils
import com.prezi.spaghetti.internal.Version
import com.prezi.spaghetti.packaging.internal.CommonJsModuleWrapper
import com.prezi.spaghetti.packaging.internal.ExternalDependencyGenerator

class CommonJsModuleWrapperTest extends WrapperTestBase {
	def "CommonJS module"() {
		def externalDependencies = ["React": "react", "\$": "jquery"]
		def originalScript = "/* Generated by Spaghetti */ return 3+4;"
		def result = new CommonJsModuleWrapper().wrap(mockParams("com.example.test", "1.0", ["com.example.alma", "com.example.bela"], externalDependencies, originalScript))

		expect:
		result == [
				';(function(){',
					'var baseUrl=__dirname;',
					'module.exports=(function(){',
						'var $=arguments[0];',
						'var React=arguments[1];',
						'var Spaghetti={',
							'getSpaghettiVersion:function(){return "' + Version.SPAGHETTI_VERSION + '";},',
							'getModuleName:function(){',
								'return "com.example.test";',
							'},',
							'getModuleVersion:function(){return "1.0";},',
							'getResourceUrl:function(resource){',
								'if(resource.substr(0,1)!="/"){',
									'resource="/"+resource;',
								'}',
								'return baseUrl+resource;',
							'},',
							'"dependencies":{',
								'"com.example.alma":arguments[2],',
								'"com.example.bela":arguments[3]',
							'}',
						'};',
						'var module=function(f){return f(Spaghetti,$,React);};',
						'/* Generated by Spaghetti */ ',
						'return{',
							'"module":(function(){\n',
							'return 3+4;\n',
							'})(),',
							'"version":"1.0",',
							'"spaghettiVersion":"' + Version.SPAGHETTI_VERSION + '"',
						'};',
					'})(require("jquery"),require("react"),require("com.example.alma"),require("com.example.bela"));',
				'})();'
		].join("")
	}

	def "CommonJS application"() {
		def dependencyTree = [
				"com.example.test": ["com.example.alma", "com.example.bela"].toSet(),
				"com.example.alma": ["com.example.bela"].toSet(),
				"com.example.bela": [].toSet()
		]
		def externals = [
				"react": "react"
		]
		def result = new CommonJsModuleWrapper().makeApplication(dependencyTree, "node_modules", "com.example.test", true, externals)

		expect:
		result == [
				'var mainModule=require("com.example.test")["module"];',
				'mainModule["main"]();\n',
		].join("")
	}
}
