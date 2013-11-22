package prezi.test.client;

import prezi.graphics.text.CharacterStyleType;

class Client {
	public static function main() {
		var layout = Modules.getLayout();
		var text = layout.createText();
		var style = layout.createCharacterStyle(CharacterStyleType.FONT_WEIGHT, "bold");
		text.insert(0, "World", [ style ]);
		text.insert(0, "Hello ", []);
		var textRenderer = Modules.getTextRenderer();
		var renderer = textRenderer.createRenderer("Text rendered with TextRenderer module: [", "]");
		trace(renderer.render(text));

		text.registerCallback(function (message:String) {
			trace("Received callback message: " + message);
		});
	}
}
