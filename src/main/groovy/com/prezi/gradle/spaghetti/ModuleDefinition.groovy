package com.prezi.gradle.spaghetti

import org.gradle.api.Named

/**
 * Created by lptr on 12/11/13.
 */
class ModuleDefinition implements Named {
	String name
	Map<String, ServiceDefinition> services = [:]
	String namespace = ""

	ModuleDefinition(String name) {
		this.name = name
	}

	void namespace(String namespace) {
		this.namespace = namespace
	}

	void service(String name, Closure cl)
	{
		def service = new ServiceDefinition(name)
		cl.delegate = service
		cl.resolveStrategy = Closure.DELEGATE_FIRST
		cl.run()
		services.put(name, service)
	}
}
