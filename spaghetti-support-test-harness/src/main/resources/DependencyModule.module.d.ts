export enum Fruit {
	Apple,
	Pear,
	Plum
}

export enum Prime {
	First = 2,
	Second = 3,
	Third = 5
}

export interface Point2d {
	x: number;
	y: number;
}

export module DependentConstant {
	const A: string;
}

export module DependencyModule {
	function getPoint(): Point2d;
	function createPointViaCallback(x: number, y: number, callback: (Point2d) => void): void;
}

export as namespace spaghetti_test_dependency;
