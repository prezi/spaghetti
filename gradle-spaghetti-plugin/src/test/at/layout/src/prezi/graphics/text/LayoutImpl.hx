package prezi.graphics.text;

class LayoutImpl implements Layout {
	public function new() {
	}

	public function createText():Text {
		return new TextImpl();
	}

	public function createCharacterStyle(type:CharacterStyleType, value:Dynamic):CharacterStyle {
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
		for (style in withStyles) {
			trace("Got style: " + style.getType().name() + " = " + style.getValue());
		}
		text = text.substr(0, offset) + textToInsert + text.substr(offset);
	}

	public function delete(offset:Int, end:Int)
	{
		text = text.substr(0, offset) + text.substr(end);
	}

	public function registerCallback(callback:String->Void) {
		callback("Calling callback");
	}
}

class CharacterStyleImpl implements CharacterStyle {
	var type:CharacterStyleType;
	var value:Dynamic;

	public function new(type:CharacterStyleType, value:Dynamic) {
		this.type = type;
		this.value = value;
	}

	public function getType():CharacterStyleType {
		return type;
	}
	public function setType(type:CharacterStyleType) {
		this.type = type;
	}

	public function getValue():Dynamic {
		return value;
	}
	public function setValue(value:Dynamic) {
		this.value = value;
	}
}
