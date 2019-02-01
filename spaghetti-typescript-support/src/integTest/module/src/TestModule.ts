import * as spaghetti_test_dependency from "spaghetti_test_dependency";
import { Numbers, Point3d } from "TestModule.module";

declare var libWithVersion: any;

export class TestModule {
	static addTwoNumbers(a:number, b:number):number {
		return a + b;
	}

	static getNextEnumValue(value:spaghetti_test_dependency.Fruit):spaghetti_test_dependency.Fruit {
		return value + 1;
	}

	static getPositionInPrimes(value:spaghetti_test_dependency.Prime):string {
		return spaghetti_test_dependency.Prime[value];
	}

	static getValueOfTwo():string {
		return Numbers.TWO;
	}

	static getValueOfDependentConstant():string {
		return spaghetti_test_dependency.DependentConstant.A;
	}

	static createPoint3dWithGivenValues(x:number, y:number, z:number):Point3d {
		return {
			x: x,
			y: y,
			z: z
		};
	}

	static getPointFromDependencyModule():spaghetti_test_dependency.Point2d {
		return spaghetti_test_dependency.DependencyModule.getPoint();
	}

	static getPointFromDependencyModuleViaCallback(x:number, y:number):spaghetti_test_dependency.Point2d {
		var returnedPoint:spaghetti_test_dependency.Point2d = null;
		spaghetti_test_dependency.DependencyModule.createPointViaCallback(x, y, (point:spaghetti_test_dependency.Point2d) => {
			returnedPoint = point;
		});
		return returnedPoint;
	}

	static returnPointViaCallback(x:number, y:number, z:number, callback:(point:Point3d)=>void) {
		callback({x: x, y: y, z: z});
	}

    static getExternalDependencyVersion(): string {
		return libWithVersion.version;
    }
}
