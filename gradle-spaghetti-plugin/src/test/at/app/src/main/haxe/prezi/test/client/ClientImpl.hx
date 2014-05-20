package prezi.test.client;

import prezi.graphics.text.CharacterStyleType;
import prezi.graphics.text.Layout;
import prezi.graphics.text.Values;
import prezi.graphics.text.render.TextRenderer;

class ClientImpl implements Client {
	public var layout(default, null):Layout;
	public var textRenderer(default, null):TextRenderer;
	public function new(config:SpaghettiModuleConfiguration, layout:Layout, textRenderer:TextRenderer) {
		this.layout = layout;
		this.textRenderer = textRenderer;
		trace("App name: " + config.getName());
	}

	public function main() {
		var text = layout.createText();
		var style:prezi.graphics.text.CharacterStyle = { type: CharacterStyleType.FONT_WEIGHT, value: "bold" };
		style.value = "normal";
		text.insert(0, "World", [ style ]);
		text.insert(0, Values.HI, []);
		var renderer = textRenderer.createRenderer("Text rendered with TextRenderer module: [", "]");
		trace(renderer.render(text));

		var testStuff = layout.createTestStuff();
		testStuff.registerCallback(function (message:String) {
			trace("Received callback message: " + message);
		});
		trace("TestStuff.doSomething(): " + testStuff.doSomething("pre", "text", "pos"));

		var canvas = untyped __js__("document.getElementById('canvas')");
		testStuff.drawSomething(canvas);

		// 	doAsync(callback:String->(Int->String)->Void, converter:Int->String) {
		var result = testStuff.doAsync(callback, function(value:Int):String { return Std.string(value + 1); });
		trace("doAync() returned: " + result);

		trace("Haxe resource: " + layout.getResource());
		trace("TypeScript resource: " + textRenderer.getResource());
	}

	function callback(name:String, converter:Int->String)
	{
		trace('Name: ${name}, converting 42: ${converter(42)}');
	}
}
