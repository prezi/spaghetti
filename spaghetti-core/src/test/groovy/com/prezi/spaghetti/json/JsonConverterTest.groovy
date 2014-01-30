package com.prezi.spaghetti.json

import com.prezi.spaghetti.GlobalScope
import com.prezi.spaghetti.ModuleConfigurationParser
import com.prezi.spaghetti.ModuleDefinition
import com.prezi.spaghetti.json.JsonConverter
import groovy.json.JsonOutput
import org.junit.Test

/**
 * Created by lptr on 28/01/14.
 */
class JsonConverterTest {
	@Test
	public void test() {
		def ctx = ModuleConfigurationParser.parse(getClass().getResource("/Test.module").text, "Test.module")
		def module = new ModuleDefinition(ctx, "0.0", "input", new GlobalScope([:]))
		println JsonOutput.prettyPrint(JsonOutput.toJson(JsonConverter.toJsonMap(module)))
	}
}
