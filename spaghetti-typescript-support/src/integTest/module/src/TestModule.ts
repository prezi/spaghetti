module spaghetti.test {

export class TestModule {
	static addTwoNumbers(a:number, b:number):number {
		return a + b;
	}

	static getNextEnumValue(value:spaghetti.test.dependency.Fruit):spaghetti.test.dependency.Fruit {
		return value + 1;
	}

	static getValueOfTwo():string {
		return Numbers.TWO;
	}

	static createPoint3dWithGivenValues(x:number, y:number, z:number):Point3d {
		return {
			x: x,
			y: y,
			z: z
		};
	}

	static getPointFromDependencyModule():spaghetti.test.dependency.Point2d {
		return spaghetti.test.dependency.DependencyModule.getPoint();
	}

	static getPointFromDependencyModuleViaCallback(x:number, y:number):spaghetti.test.dependency.Point2d {
		var returnedPoint:spaghetti.test.dependency.Point2d = null;
		spaghetti.test.dependency.DependencyModule.createPointViaCallback(x, y, (point:spaghetti.test.dependency.Point2d) => {
			returnedPoint = point;
		});
		return returnedPoint;
	}

	static returnPointViaCallback(x:number, y:number, z:number, callback:(point:Point3d)=>void) {
		callback({x: x, y: y, z: z});
	}
}
}
