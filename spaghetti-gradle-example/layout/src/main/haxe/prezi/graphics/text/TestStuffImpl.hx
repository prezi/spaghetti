package prezi.graphics.text;

class TestStuffImpl<Pre, Post> implements TestStuff<Pre, Post> {
	public function new() {}
	public function registerCallback(callback:String->Void) {
		callback("Calling callback");
	}
	public function doVoidCallback(callback:Void->Int) {
		return callback();
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
