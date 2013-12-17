package prezi.test.client;

import prezi.graphics.text.CharacterStyleType;

class Client {
	public static function main() {
		var layout = Modules.getLayout();
		var text = layout.createText();
		var style:prezi.graphics.text.CharacterStyle = { type: CharacterStyleType.FONT_WEIGHT, value: "bold" };
		style.value = "normal";
		text.insert(0, "World", [ style ]);
		text.insert(0, "Hello ", []);
		var textRenderer = Modules.getTextRenderer();
		var renderer = textRenderer.createRenderer("Text rendered with TextRenderer module: [", "]");
		trace(renderer.render(text));

		var testStuff = layout.createTestStuff();
		testStuff.registerCallback(function (message:String) {
			trace("Received callback message: " + message);
		});
		trace("TestStuff.doSomething(): " + testStuff.doSomething("pre", "text", "pos"));

		var canvas = untyped __js__("document.getElementById('canvas')");
		testStuff.drawSomething(canvas);
	}
}
