package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.AstSpecification
import com.prezi.spaghetti.ast.internal.parser.ModuleParser

class KotlinConstGeneratorVisitorTest extends AstSpecification {
    def "generate"() {
        def definition = """module com.example.test

/**
 * My dear constants.
 */
        @deprecated
        const MyConstants {
            int alma = 1
            /**
             * Bela is -123.
             */
            @deprecated("lajos")
            int bela = -123
            geza = -1.23
            tibor = "tibor"
        }
        """
        def locator = mockLocator(definition)
        def parser = ModuleParser.create(locator.source)
        def module = parser.parse(mockResolver())
        def visitor = new KotlinConstGeneratorVisitor()

        expect:
        visitor.visit(module) == """/**
 * My dear constants.
 */
[deprecated]
object MyConstants {
	val alma:Int = 1
	/**
	 * Bela is -123.
	 */
	[deprecated("lajos")]
	val bela:Int = -123
	val geza:Double = -1.23
	val tibor:String = "tibor"

}
"""
    }
}

