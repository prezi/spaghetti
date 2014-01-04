package prezi.test.client;

import prezi.graphics.text.CharacterStyleType;
import prezi.graphics.text.Layout;
import prezi.graphics.text.render.TextRenderer;

class Client {
	public static function main() {
		var text = Layout.createText();
		var style:prezi.graphics.text.CharacterStyle = { type: CharacterStyleType.FONT_WEIGHT, value: "bold" };
		style.value = "normal";
		text.insert(0, "World", [ style ]);
		text.insert(0, "Hello ", []);
		var renderer = TextRenderer.createRenderer("Text rendered with TextRenderer module: [", "]");
		trace(renderer.render(text));

		var testStuff = Layout.createTestStuff();
		testStuff.registerCallback(function (message:String) {
			trace("Received callback message: " + message);
		});
		trace("TestStuff.doSomething(): " + testStuff.doSomething("pre", "text", "pos"));

		var canvas = untyped __js__("document.getElementById('canvas')");
		testStuff.drawSomething(canvas);

		// 	doAsync(callback:String->(Int->String)->Void, converter:Int->String) {
		var result = testStuff.doAsync(callback, function(value:Int):String { return Std.string(value + 1); });
		trace("doAync() returned: " + result);
	}

	static function callback(name:String, converter:Int->String)
	{
		trace('Name: ${name}, converting 42: ${converter(42)}');
	}
}
