package prezi.test.client;

import prezi.graphics.text.CharacterStyle;
import prezi.graphics.text.CharacterStyleType;
import prezi.graphics.text.Layout;
import prezi.graphics.text.Values;
import prezi.graphics.text.render.TextRenderer;

import org.hamcrest.MatchersBase;

class ClientTest extends MatchersBase {

	@Test
	public function testCreateObject() {
		var text = SpaghettiTest.getModule("prezi.graphics.text").createText();
		assertThat(text, is(not(null)));
	}

	@Test
	public function testModifyStruct() {
		var style:CharacterStyle = { type: CharacterStyleType.FONT_WEIGHT, value: "bold" };
		assertThat(style.type, is(CharacterStyleType.FONT_WEIGHT));
		assertThat(style.value, is("bold"));
		style.value = "normal";
		assertThat(style.value, is("normal"));
	}

	@Test
	public function testModifyObject() {
		var text = SpaghettiTest.getModule().layout.createText();
		var style = { type: CharacterStyleType.FONT_WEIGHT, value: "normal" };
		text.insert(0, "World", [ style ]);
		text.insert(0, " ", []);
		text.insert(0, Values.HI, []);
		assertThat(text.getRawText(), is("Hello World"));
	}

	@Test
	public function testTypeScript() {
		var text = SpaghettiTest.getModule().layout.createText();
		text.insert(0, "Hello World", []);
		var renderer = SpaghettiTest.getModule().textRenderer.createRenderer("Text rendered with TextRenderer module: [", "]");
		assertThat(renderer.render(text), is("Text rendered with TextRenderer module: [Hello World] (12)"));
	}

	@Test
	public function testSyncCallback() {
		var testStuff = SpaghettiTest.getModule().layout.createTestStuff();
		var callbackMessage = null;
		testStuff.registerCallback(function (message:String) {
			callbackMessage = message;
		});
		assertThat(callbackMessage, is("Calling callback"));
	}

	@Test
	public function testDoSomething() {
		var testStuff = SpaghettiTest.getModule().layout.createTestStuff();
		assertThat(testStuff.doSomething("pre", "text", "pos"), is("pre-text-pos"));
	}

	@Test
	public function testAsync() {
		var testStuff = SpaghettiTest.getModule().layout.createTestStuff();
		// 	doAsync(callback:String->(Int->String)->Void, converter:Int->String) {
		var cbName:String = null;
		var cbConverted:String = null;
		var callback = function(name:String, converter:Int->String) {
			cbName = name;
			cbConverted = converter(42);
		};
		var result = testStuff.doAsync(callback, function(value:Int):String { return Std.string(value + 1); });
		assertThat(cbName, is("Calling async callback"));
		assertThat(cbConverted, is("43"));
		assertThat(result, is(12));
	}

	@Test
	public function testResourceUrls() {
		assertThat(SpaghettiTest.getModule().layout.getResource(), is("./modules/prezi.graphics.text/sample.txt"));
		assertThat(SpaghettiTest.getModule().textRenderer.getResource(), is("./modules/prezi.graphics.text.render/some-resource.txt"));
	}
}