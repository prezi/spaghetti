declare module spaghetti.test.dependency {

	enum Fruit {
		Apple,
		Pear,
		Plum
	}

	enum Prime {
		First = 2,
		Second = 3,
		Third = 5
	}

	interface Point2d {
		x: number;
		y: number;
	}

	module DependencyModule {
		function getPoint(): Point2d;
		function createPointViaCallback(x: number, y: number, callback: (Point2d) => void): void;
	}
}
