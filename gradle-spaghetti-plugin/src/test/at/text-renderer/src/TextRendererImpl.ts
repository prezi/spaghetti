module prezi.graphics.text.render {

class TextRendererImpl implements TextRenderer {
	constructor() {}
	createRenderer(prefix:string, suffix:string):Renderer {
		return new RendererImpl(prefix, suffix);
	}
}

class RendererImpl implements Renderer {
	prefix:string;
	suffix:string;

	constructor(prefix:string, suffix:string) {
		this.prefix = prefix;
		this.suffix = suffix;
	}
    render(text:prezi.graphics.text.Text):string {
		return this.prefix + text.getRawText() + this.suffix;
	}
}

}
