var testPoint = {
	x: 1,
	y: 2
};

return {
	getNumber: function () {
		return 42;
	},
	getPoint: function () {
		return testPoint;
	},
	createPointViaCallback: function (x, y, callback) {
		callback({ x: x, y: y});
	}
};
