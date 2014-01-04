package prezi.graphics.text;

class LayoutImpl implements Layout {
	public function new() {
	}

	public function createText():Text {
		return new TextImpl();
	}

	public function createTestStuff():TestStuff<String, String>
	{
		return new TestStuffImpl<String, String>();
	}
}

class TestStuffImpl<Pre, Post> implements TestStuff<Pre, Post> {
	public function new() {}
	public function registerCallback(callback:String->Void) {
		callback("Calling callback");
	}
	public function doAsync(callback:String->(Int->String)->Void, converter:Int->String) {
		callback("Calling async callback", converter);
		return 12;
	}
	public function doSomething(pre:Pre, text:String, post:Post):String {
		return '${pre}-${text}-${post}';
	}
	public function drawSomething(canvas:js.html.CanvasElement) {
		canvas.getContext("2d").fillRect(10, 10, 100, 50);
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
