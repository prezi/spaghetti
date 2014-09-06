package prezi.graphics.text;

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

	public function insert(offset:Int, textToInsert:String, withStyles:Null<Array<CharacterStyle>>)
	{
		for (style in withStyles) {
			trace("Got style: " + style.type.name() + " = " + style.value);
		}
		text = text.substr(0, offset) + textToInsert + text.substr(offset);
	}

	public function delete(offset:Int, end:Int)
	{
		text = text.substr(0, offset) + text.substr(end);
	}
}
