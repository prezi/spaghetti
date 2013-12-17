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
	}
}
