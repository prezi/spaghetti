package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.generator.ConstGeneratorSpecification

class KotlinConstGeneratorVisitorTest extends ConstGeneratorSpecification {
    def "generate"() {
        def definition = """
/**
 * My dear constants.
 */
@deprecated
const MyConstants {
    alma: int = 1;
    /**
     * Bela is -123.
     */
    @deprecated("lajos")
    bela: int = -123;
    geza = -1.23;
    tibor = "tibor";
}
"""

        def result = parseAndVisitConst(definition, new KotlinConstGeneratorVisitor())

        expect:
        result == """/**
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

