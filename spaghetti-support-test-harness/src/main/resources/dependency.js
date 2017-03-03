var testPoint = {
	x: 1,
	y: 2
};

var module = {
	getNumber: function () {
		return 42;
	},
	getPoint: function () {
		return testPoint;
	},
	createPointViaCallback: function (x, y, callback) {
		callback({ x: x, y: y});
	},
	Fruit: (function(Fruit) {
		Fruit[Fruit.Apple = 0] = "Apple";
		Fruit[Fruit.Pear = 1] = "Pear";
		Fruit[Fruit.Plum = 2] = "Plum";
		return Fruit;
	})({}),
	Prime: (function(Prime) {
		Prime[Prime.First = 2] = "First";
		Prime[Prime.Second = 3] = "Second";
		Prime[Prime.Third = 5] = "Third";
		return Prime;
	})({})
};
// Extra DependencyModule is needed to make TypeScript's direct namespace linking
// compatible with Spaghetti's generated proxy code.
module.DependencyModule = {
	getNumber: module.getNumber,
	getPoint: module.getPoint,
	createPointViaCallback: module.createPointViaCallback,
}
return module;
