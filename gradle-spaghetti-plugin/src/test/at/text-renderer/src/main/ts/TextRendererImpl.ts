module prezi.graphics.text.render {


export class TextRendererImpl implements TextRenderer {
	constructor() {}
	createRenderer(prefix:string, suffix:string):Renderer {
		return new RendererImpl(prefix, suffix);
	}
	getResource():string {
		return Spaghetti.getResourceUrl("some-resource.txt");
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
		return this.prefix + text.getRawText() + this.suffix + " (" + prezi.graphics.text.Values.HELLO + ")";
	}

	f() {
		var x = Values.MAX_LENGTH;
		var y = text.Values.HI;

	}

}

export var Values = {
	MAX_LENGTH: 5, 
	PLACEHOLDER: 'string',
	DEFAULT_RENDERER: new RendererImpl('x','y')
};


}
