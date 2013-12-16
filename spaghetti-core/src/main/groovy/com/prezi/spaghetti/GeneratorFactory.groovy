package com.prezi.spaghetti

/**
 * Created by lptr on 23/11/13.
 */
public interface GeneratorFactory {
	String getPlatform()
	String getDescription()
	Generator createGenerator(ModuleConfiguration configuration)
	Map<String, String> getExterns()
}
