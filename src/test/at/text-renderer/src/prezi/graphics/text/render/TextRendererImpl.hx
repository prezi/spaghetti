package prezi.graphics.text.render;

class TextRendererImpl implements TextRenderer {
	public function new() {}
	public function createRenderer():Renderer {
		return new RendererImpl();
	}
}

class RendererImpl implements Renderer {
	public function new() {}
    public function render(text:prezi.graphics.text.Text):String {
		return "Text: [" + text.getRawText() + "]";
	}
}
