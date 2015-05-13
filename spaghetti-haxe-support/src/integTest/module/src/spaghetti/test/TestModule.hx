package spaghetti.test;

import spaghetti.test.dependency.DependencyModule;
import spaghetti.test.dependency.Fruit;
import spaghetti.test.dependency.Point2d;

class TestModule {
	public static function addTwoNumbers(a:Int, b:Int):Int {
		return a + b;
	}

	public static function getNextEnumValue(value:Fruit):Fruit {
		return Fruit.fromValue(value.value() + 1);
	}

	public static function getValueOfTwo():String {
		return Numbers.TWO;
	}

	public static function createPoint3dWithGivenValues(x:Int, y:Int, z:Int):Point3d {
		return {
			x: x,
			y: y,
			z: z
		};
	}

	public static function getPointFromDependencyModule():Point2d {
		return DependencyModule.getPoint();
	}

	public static function getPointFromDependencyModuleViaCallback(x:Int, y:Int):Point2d {
		var returnedPoint:Point2d = null;
		spaghetti.test.dependency.DependencyModule.createPointViaCallback(x, y, function (point:Point2d) {
			returnedPoint = point;
		});
		return returnedPoint;
	}

	public static function returnPointViaCallback(x:Int, y:Int, z:Int, callback:Point3d->Void):Void {
		callback({x: x, y: y, z: z});
	}

	public static function getExternalDependencyVersion(): String {
	    return untyped __js__('libWithVersion.version');
	}
}
