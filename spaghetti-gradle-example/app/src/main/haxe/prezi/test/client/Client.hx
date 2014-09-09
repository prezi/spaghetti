package prezi.test.client;

import prezi.graphics.core.Core;
import prezi.graphics.text.CharacterStyleType;
import prezi.graphics.text.Layout;
import prezi.graphics.text.Values;
import prezi.graphics.text.render.TextRenderer;

class Client {
	public static function main() {
		trace("App name: " + SpaghettiConfiguration.getName());
		trace("App version: " + SpaghettiConfiguration.getVersion());
		trace("App built by Spaghetti version " + SpaghettiConfiguration.getSpaghettiVersion());
		var text = prezi.graphics.text.Layout.createText();
		var style:prezi.graphics.text.CharacterStyle = { type: CharacterStyleType.FONT_WEIGHT, value: "bold" };
		style.value = "normal";
		text.insert(0, "World", [ style ]);
		text.insert(0, Values.HI, []);
		var renderer = prezi.graphics.text.render.TextRenderer.createRenderer("Text rendered with TextRenderer module: [", "]");
		trace(renderer.render(text));

		var testStuff = prezi.graphics.text.Layout.createTestStuff();
		testStuff.registerCallback(function (message:String) {
			trace("Received callback message: " + message);
		});
		trace("TestStuff.doSomething(): " + testStuff.doSomething("pre", "text", "pos"));

		if (untyped __js__("typeof(document)") != "undefined") {
			var canvas = untyped __js__("document.getElementById('canvas')");
			testStuff.drawSomething(canvas);
		}

		// 	doAsync(callback:String->(Int->String)->Void, converter:Int->String) {
		var result = testStuff.doAsync(callback, function(value:Int):String { return Std.string(value + 1); });
		trace("doAync() returned: " + result);

		trace("Haxe resource: " + prezi.graphics.text.Layout.getResource());
		trace("TypeScript resource: " + prezi.graphics.text.render.TextRenderer.getResource());
	}

	static function callback(name:String, converter:Int->String)
	{
		trace('Name: ${name}, converting 42: ${converter(42)}');
	}
}
