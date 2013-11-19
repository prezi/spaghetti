package prezi.graphics.text.render;

class TextRendererImpl implements TextRenderer {
	public function new() {}
	public function createRenderer(prefix:String, suffix:String):Renderer {
		return new RendererImpl(prefix, suffix);
	}
}

class RendererImpl implements Renderer {
	var prefix:String;
	var suffix:String;

	public function new(prefix:String, suffix:String) {
		this.prefix = prefix;
		this.suffix = suffix;
	}
    public function render(text:prezi.graphics.text.Text):String {
		return prefix + text.getRawText() + suffix;
	}
}
