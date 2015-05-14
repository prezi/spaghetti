package spaghetti.test

import spaghetti.test.dependency.*

public class TestModule {
	companion object {
		public fun addTwoNumbers(a:Int, b:Int): Int {
			return a + b
		}

		public fun getNextEnumValue(value:Fruit):Fruit {
			return ((value as Int) + 1) as Fruit
		}

		public fun getPositionInPrimes(value:Prime):String {
        	return Prime.getName(value)
        }

		public fun getValueOfTwo():String {
			return Numbers.TWO
		}

		public fun createPoint3dWithGivenValues(x:Int, y:Int, z:Int):Point3d {
			return object : Point3d {
				override val x = x
				override val y = y
				override val z = z
			}
		}

		public fun getPointFromDependencyModule():Point2d {
			return DependencyModule.getPoint()
		}

		public fun getPointFromDependencyModuleViaCallback(x:Int, y:Int):Point2d {
            var result:Point2d? = null
			DependencyModule.createPointViaCallback(x, y, { point ->
				result = point
			})
            return result!!
		}

		public fun returnPointViaCallback(x:Int, y:Int, z:Int, callback:(Point3d)->Unit) {
            callback(object : Point3d {
                override val x = x
                override val y = y
                override val z = z
            });
		}

		public fun getExternalDependencyVersion():String {
		    return js("libWithVersion.version")
		}
	}
}
