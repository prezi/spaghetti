module prezi.graphics.text.render {


export class TextRendererImpl implements TextRenderer {
	config:SpaghettiModuleConfiguration;
	layout:prezi.graphics.text.Layout;
	constructor(config:SpaghettiModuleConfiguration, layout:prezi.graphics.text.Layout) {
		this.config = config;
		this.layout = layout;
		console.log("Text renderer name: " + config.getName());
		console.log("Core stuff: " + Core.giveMeANumber());
	}
	createRenderer(prefix:string, suffix:string):Renderer {
		return new RendererImpl(this.layout, prefix, suffix);
	}
	getResource():string {
		return this.config.getResourceUrl("some-resource.txt");
	}
}


export class RendererImpl implements Renderer {
	prefix:string;
	suffix:string;
	testStuff:prezi.graphics.text.TestStuff<string, string>;


	constructor(layout:prezi.graphics.text.Layout, prefix:string, suffix:string) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.testStuff = layout.createTestStuff();
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
	PLACEHOLDER: 'string'
};


}
