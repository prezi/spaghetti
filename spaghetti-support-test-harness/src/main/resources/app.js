var expect = require("chai").expect;

return {
	main: function () {
		var module = Spaghetti["dependencies"]["spaghetti.test"]["module"];
		describe("Module", function () {

			describe("#add()", function () {
				it("should add numbers", function () {
					expect(module.add(1,2)).to.equal(3);
				});
			});

			describe("#getNext()", function () {
				it("should return the next element in the DependentEnum enum", function () {
					expect(module.getNext(0 /* ALMA */)).to.equal(1 /* BELA */);
				});
			});
		});
	}
};
