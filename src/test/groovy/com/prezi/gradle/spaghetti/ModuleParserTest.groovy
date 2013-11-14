package com.prezi.gradle.spaghetti

import com.prezi.gradle.spaghetti.parse.ModuleParser
import org.junit.Test

/**
 * Created by lptr on 14/11/13.
 */
class ModuleParserTest {
	@Test
	void testEmpty() {
		def moduleDef = new ModuleParser("""
module Layout {
	type Text {
		define insert(offset:int)
	}
}
""").parse()
		println "Parsed ${moduleDef}"
	}
}
