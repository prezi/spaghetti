package com.prezi.spaghetti.typescript.bundle

import com.prezi.spaghetti.generator.ConstGeneratorSpecification
import com.prezi.spaghetti.typescript.bundle.TypeScriptEnumDenormalizer

class TypeScriptEnumDenormalizerTest extends ConstGeneratorSpecification {
	def "denormalize enums"() {
		def jsSource = """var Adventurous;
(function (Adventurous) {
    Adventurous[Adventurous["A"] = 0] = "A";
    Adventurous[Adventurous["A_B_C"] = someVal["someKey"]] = "A_B_C";
    Adventurous[Adventurous["ƒƒƒ"] = someVal] = "ƒƒƒ";
    Adventurous[Adventurous["no-pe"] = 5] = "no-pe";
})(Adventurous || (Adventurous = {}));
Adventurous.A;
Adventurous.ƒƒƒ;
Adventurous["no-pe"];
"""

		def denormalized = """var Adventurous;
(function (Adventurous) {
    Adventurous[Adventurous.A = 0] = "A";
    Adventurous[Adventurous.A_B_C = someVal["someKey"]] = "A_B_C";
    Adventurous[Adventurous.ƒƒƒ = someVal] = "ƒƒƒ";
    Adventurous[Adventurous["no-pe"] = 5] = "no-pe";
})(Adventurous || (Adventurous = {}));
Adventurous.A;
Adventurous.ƒƒƒ;
Adventurous["no-pe"];
"""

		expect:
		TypeScriptEnumDenormalizer.denormalize(jsSource) == denormalized
	}
}
