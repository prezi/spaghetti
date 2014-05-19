package prezi.graphics.text;

import org.hamcrest.MatchersBase;

class TextImplTest extends MatchersBase {
	@Test
	public function testCreateText() {
		var text = new TextImpl();
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
	public function testModifyText() {
		var text = new TextImpl();
		var style = { type: CharacterStyleType.FONT_WEIGHT, value: "normal" };
		text.insert(0, "World", [ style ]);
		text.insert(0, " ", []);
		text.insert(0, Values.HI, []);
		assertThat(text.getRawText(), is("Hello World"));
	}
}
