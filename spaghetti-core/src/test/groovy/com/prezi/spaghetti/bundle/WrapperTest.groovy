package com.prezi.spaghetti.bundle

import spock.lang.Specification

/**
 * Created by lptr on 16/05/14.
 */
class WrapperTest extends Specification {
	def "amd module"() {
		def originalScript = "__spaghetti(function(__config){});"
		def result = Wrapper.AMD.wrap("com.example.test", ["com.example.alma", "com.example.bela"], originalScript)

		expect:
		result == [
		        'define(["require","com.example.alma","com.example.bela"],function(){',
					'var moduleUrl=arguments[0]["toUrl"]("com.example.test.js");',
					'var baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf("/")+1);',
					'var __config={',
						'"__baseUrl":baseUrl,',
						'"__modules":{',
							'"require":arguments[0],',
							'"com.example.alma":arguments[1],',
							'"com.example.bela":arguments[2]',
						'}',
					'};',
					'var __spaghetti=function(){',
						'return arguments[0](__config);',
					'};',
					'return ', originalScript,
				'});'
		].join("")
	}

	def "amd application"() {
		def result = Wrapper.AMD.makeApplication("mods", ["com.example.alma", "com.example.bela", "com.example.test"], "com.example.test")

		expect:
		result == [
				'require["config"]({',
					'"baseUrl":".",',
					'"paths":{',
						'"com.example.alma": "mods/com.example.alma/com.example.alma",',
						'"com.example.bela": "mods/com.example.bela/com.example.bela",',
						'"com.example.test": "mods/com.example.test/com.example.test"',
					'}',
				'});',
		        'require(["com.example.test"],function(__mainModule){',
					'__mainModule["__module"]["main"]();',
				'});'
		].join("")
	}
}
