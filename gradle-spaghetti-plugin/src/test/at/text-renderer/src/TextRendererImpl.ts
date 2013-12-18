module prezi.graphics.text.render {

export class TextRendererImpl implements TextRenderer {
	constructor() {}
	createRenderer(prefix:string, suffix:string):Renderer {
		return new RendererImpl(prefix, suffix);
	}
}

export class RendererImpl implements Renderer {
	prefix:string;
	suffix:string;
	testStuff:prezi.graphics.text.TestStuff<string, string>;

	constructor(prefix:string, suffix:string) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.testStuff = Layout.createTestStuff();
	}

    render(text:prezi.graphics.text.Text):string {
		return this.prefix + text.getRawText() + this.suffix;
	}
}

}
