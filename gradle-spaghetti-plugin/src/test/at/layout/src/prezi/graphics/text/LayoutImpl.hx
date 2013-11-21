package prezi.graphics.text;

class LayoutImpl implements Layout {
	public function new() {
	}

	public function createText():Text {
		return new TextImpl();
	}

	public function createCharacterStyle(type:String, value:Dynamic):CharacterStyle {
		return new CharacterStyleImpl(type, value);
	}
}

class TextImpl implements Text
{
	var text:String;

	public function new()
	{
		this.text = "";
	}

	public function getRawText():String
	{
		return text;
	}

	public function insert(offset:Int, textToInsert:String, withStyles:Array<CharacterStyle>)
	{
		trace("Got styles: " + withStyles);
		text = text.substr(0, offset) + textToInsert + text.substr(offset);
	}

	public function delete(offset:Int, end:Int)
	{
		text = text.substr(0, offset) + text.substr(end);
	}

	public function getInternals():Dynamic {
		return "Internal implementation stuff here";
	}
}

class CharacterStyleImpl implements CharacterStyle {
	var type:String;
	var value:Dynamic;

	public function new(type:String, value:Dynamic) {
		this.type = type;
		this.value = value;
	}

	public function getType():String {
		return type;
	}

	public function getValue():Dynamic {
		return value;
	}
}
