declare module spaghetti.test {

	import Fruit = spaghetti.test.dependency.Fruit;
	import Prime = spaghetti.test.dependency.Prime;
	import Point2d = spaghetti.test.dependency.Point2d;

	/**
	 * This enum should be exported on the compiled module.
	 */
	enum Exported {
		Target = 42,
		Another = 12
	}

	const Numbers: {
		ZERO: number,
		ONE: number,
		TWO: string,
	}

	interface Point3d extends Point2d {
		z: number;
	}

	/**
	 * Add the two numbers and return the result.
	 */
	function addTwoNumbers(a: number, b: number): number;

	/**
	 * Take the given enum value, and return the next value in the Fruit enum.
	 * The enum is declared in the DependencyModule module.
	 */
	function getNextEnumValue(value: Fruit): Fruit;

	/**
	 * Return the position for the first three primes.
	 */
	function getPositionInPrimes(value: Prime): string;

	/**
	 * Return the value of the constant "TWO" defined in right above Numbers.
	 */
	function getValueOfTwo(): string;

	function getValueOfDependentConstant(): string;

	/**
	 * Create an instance of the Point3d type, and assign the parameters to its fields.
	 */
	function createPoint3dWithGivenValues(x: number, y: number, z: number): Point3d;

	/**
	 * Return the Point2d created by DependencyModule.getPoint().
	 */
	function getPointFromDependencyModule(): Point2d;

	/**
	 * Call DependencyModule.createStructViaCallback() with the given x and y values,
	 * and return the Point2d created.
	 */
	function getPointFromDependencyModuleViaCallback(x: number, y: number): Point2d;

	/**
	 * Construct a new Point3d with the given values, and pass it to the callback.
	 */
	function returnPointViaCallback(x: number, y: number, z: number, callback: (p: Point3d) => void): void;

	/**
	 * Call into external dependency to get its version.
	 */
	function getExternalDependencyVersion(): string;

}
