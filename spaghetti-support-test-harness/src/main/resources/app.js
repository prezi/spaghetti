var expect = require("chai").expect;

return {
	main: function () {
		var module = Spaghetti["dependencies"]["spaghetti.test"]["module"];
		var dependencyModule = Spaghetti["dependencies"]["spaghetti.test.dependency"]["module"];
		describe("Module", function () {

			describe("#addTwoNumbers()", function () {
				it("should add numbers", function () {
					expect(module.addTwoNumbers(1,2)).to.equal(3);
				});
			});

			describe("#getNextEnumValue()", function () {
				it("should return the next element in the DependentEnum enum", function () {
					expect(module.getNextEnumValue(0 /* Apple */)).to.equal(1 /* Pear */);
				});
			});

			describe("#getValueOfTwo()", function () {
				it("should return the value of the constant Numbers.TWO", function () {
					expect(module.getValueOfTwo()).to.equal("two");
				});
			});

			describe("#createPoint3dWithGivenValues()", function () {
				it("should create a struct with the given values", function () {
					var point3d = module.createPoint3dWithGivenValues(1, 2, 3);
					expect(point3d.x).to.equal(1);
					expect(point3d.y).to.equal(2);
					expect(point3d.z).to.equal(3);
				});
			});

			describe("#getPointFromDependencyModule()", function () {
				it("should return the same object as the one returned by the dependency module", function () {
					var dependencyPoint = dependencyModule.getPoint();
					var point = module.getPointFromDependencyModule();
					expect(point).to.equal(dependencyPoint);
				});
			});

			describe("#getPointFromDependencyModuleViaCallback()", function () {
				it("should return the point", function () {
					var point = module.getPointFromDependencyModuleViaCallback(1, 2);
					expect(point).to.exist;
					expect(point.x).to.equal(1);
					expect(point.y).to.equal(2);
				});
			});

			describe("#returnPointViaCallback()", function () {
				it("should return a point via the callback", function (done) {
					module.returnPointViaCallback(1, 2, 3, function (point) {
						expect(point).to.exist;
						expect(point.x).to.equal(1);
						expect(point.y).to.equal(2);
						expect(point.z).to.equal(3);
						done();
					});
				});
			});

			describe("#getExternalDependencyVersion()", function () {
				it("should return the right version for external dependency", function () {
					expect(module.getExternalDependencyVersion()).to.equal(require("chai").version);
				})
			});
		});
	}
};
