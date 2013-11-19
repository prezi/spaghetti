package com.prezi.gradle.spaghetti

import com.prezi.spaghetti.ModuleParser
import org.junit.Test
/**
 * Created by lptr on 14/11/13.
 */
class ModuleParserTest {
	@Test
	void testEmpty() {
		def moduleDef = ModuleParser.parse("""
/**
 * Layout module.
 */
module prezi.graphics.text.Layout {

    /**
     * Describes a block of text.
     */
    type Text {
        /**
         * Inserts the given String at <code>offset</code>.
         */
        void insert(int offset, String text)
        void delete(int offset, int end)
        String getRawText()
    }

    Text createText()
}
""")
		println "Parsed ${moduleDef}"
	}
}
