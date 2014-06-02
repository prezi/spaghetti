module prezi.graphics.text.render {


export class TextRenderer implements ITextRenderer {
	layout:prezi.graphics.text.Layout;
	constructor(core:prezi.graphics.core.Core, layout:prezi.graphics.text.Layout) {
		this.layout = layout;
		console.log("Text renderer name: " + SpaghettiConfiguration.getName());
		console.log("Core stuff: " + prezi.graphics.core.Core.giveMeANumber());
	}
	createRenderer(prefix:string, suffix:string):Renderer {
		return new RendererImpl(this.layout, prefix, suffix);
	}
	getResource():string {
		return SpaghettiConfiguration.getResourceUrl("some-resource.txt");
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
		var generic:prezi.graphics.text.Generic<string> = {
			element: "lajos"
		};
		console.log("Generic: ", generic.element);
	}

    render(text:prezi.graphics.text.Text):string {
		return this.prefix + text.getRawText() + this.suffix + " (" + prezi.graphics.text.Values.HELLO + ")";
	}

	f() {
		var x = Values.MAX_LENGTH;
		var y = text.Values.HI;

	}

}

}
