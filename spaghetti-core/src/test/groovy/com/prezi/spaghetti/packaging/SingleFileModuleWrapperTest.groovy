package com.prezi.spaghetti.packaging

import com.prezi.spaghetti.generator.internal.InternalGeneratorUtils
import com.prezi.spaghetti.internal.Version
import com.prezi.spaghetti.packaging.internal.ExternalDependencyGenerator
import com.prezi.spaghetti.packaging.internal.SingleFileModuleWrapper

class SingleFileModuleWrapperTest extends WrapperTestBase {
	def "Single file module"() {
		def externalDependencies = ["React": "react", "\$": "jquery"]
		def importedExternalDependencyVars = ExternalDependencyGenerator.getImportedVarNames(externalDependencies.keySet());
		def originalScript = "/* Generated by Spaghetti */ " + InternalGeneratorUtils.bundleJavaScript("", importedExternalDependencyVars)
		def result = new SingleFileModuleWrapper().wrap(mockParams("com.example.test", "1.0", ["com.example.alma", "com.example.bela"], ["com.lazy.first"], externalDependencies, originalScript))

		expect:
		result == [
				'function(){',
					'var baseUrl=__dirname;',
		        	'return(function(){',
						'var $=arguments[0];',
						'var React=arguments[1];',
						'var module=(function(dependencies){',
							'return function(init){',
								'return init.call({},(function(){',
									'return{',
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
											'"com.example.alma":dependencies[2],',
											'"com.example.bela":dependencies[3],',
											'"com.lazy.first":dependencies[4]',
										'}',
									'};',
								'})(),$,React);',
							'};',
						'})(arguments);',
						'/* Generated by Spaghetti */ ',
						'return{',
							'"module":(function(){return module(function(Spaghetti,$,React) {\n\n})\n\n})(),',
							'"version":"1.0",',
							'"spaghettiVersion":"' + Version.SPAGHETTI_VERSION + '"',
						'};',
					'}).apply({},arguments);',
					'}'
		].join("")
	}

	def "Single file application"() {
		def dependencies = [
				"com.example.test",
				"com.example.alma",
				"com.example.bela",
		]
		def result = new SingleFileModuleWrapper().makeApplication(dependencies, "", "com.example.test", true, ["react": "react"])

		expect:
		result == [
				'var mainModule=modules["com.example.test"]["module"];',
				'mainModule["main"]();'
		].join("")
	}
}
