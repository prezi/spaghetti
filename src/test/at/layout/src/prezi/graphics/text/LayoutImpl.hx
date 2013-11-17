package prezi.graphics.text;

class LayoutImpl implements Layout {
	public function new() {
	}

	public function createText(numberOfParagraphs:Int):Text {
		return new TextImpl();
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

	public function insert(offset:Int, textToInsert:String)
	{
		text = text.substr(0, offset) + textToInsert + text.substr(offset);
	}

	public function delete(offset:Int, end:Int)
	{
		text = text.substr(0, offset) + text.substr(end);
	}
}
