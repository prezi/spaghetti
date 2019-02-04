import { Fruit, Prime, Point2d } from "spaghetti_test_dependency";
import { TestModule } from "TestModule";

export enum Exported {
	Target = 42,
	Another = 12
}

export const Numbers = {
	ZERO: 0,
	ONE: 1,
	TWO: "two",
}

export interface Point3d extends Point2d {
	z: number;
}

/**
 * Add the two numbers and return the result.
 */
export const addTwoNumbers: (a: number, b: number) => number = TestModule.addTwoNumbers;

/**
 * Take the given enum value, and return the next value in the Fruit enum.
 * The enum is declared in the DependencyModule module.
 */
export const getNextEnumValue: (value: Fruit) => Fruit = TestModule.getNextEnumValue;

/**
 * Return the position for the first three primes.
 */
export const getPositionInPrimes: (value: Prime) => string = TestModule.getPositionInPrimes;

/**
 * Return the value of the constant "TWO" defined in right above Numbers.
 */
export const getValueOfTwo: () => string = TestModule.getValueOfTwo;

export const getValueOfDependentConstant: () => string = TestModule.getValueOfDependentConstant;

/**
 * Create an instance of the Point3d type, and assign the parameters to its fields.
 */
export const createPoint3dWithGivenValues: (x: number, y: number, z: number) => Point3d = TestModule.createPoint3dWithGivenValues;

/**
 * Return the Point2d created by DependencyModule.getPoint().
 */
export const getPointFromDependencyModule: () => Point2d = TestModule.getPointFromDependencyModule;

/**
 * Call DependencyModule.createStructViaCallback() with the given x and y values,
 * and return the Point2d created.
 */
export const getPointFromDependencyModuleViaCallback: (x: number, y: number) => Point2d = TestModule.getPointFromDependencyModuleViaCallback;

/**
 * Construct a new Point3d with the given values, and pass it to the callback.
 */
export const returnPointViaCallback: (x: number, y: number, z: number, callback: (p: Point3d) => void) => void = TestModule.returnPointViaCallback;

/**
 * Call into external dependency to get its version.
 */
export const getExternalDependencyVersion: () => string = TestModule.getExternalDependencyVersion;
