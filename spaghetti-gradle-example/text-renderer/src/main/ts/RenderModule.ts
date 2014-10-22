module prezi.graphics.text.render {

export class RenderModule {
	static createRenderer(prefix:string, suffix:string):Renderer {
		console.log("Text renderer name: " + Spaghetti.getModuleName());
		console.log("Text renderer version: " + Spaghetti.getModuleVersion());
		console.log("Text renderer built by Spaghetti version " + Spaghetti.getSpaghettiVersion());
		console.log("Core stuff: " + prezi.graphics.core.Core.giveMeANumber());
		return new RendererImpl(prefix, suffix);
	}
	static getResource():string {
		return Spaghetti.getResourceUrl("some-resource.txt");
	}
	static getEnumValue(value:prezi.graphics.core.JsEnum):string {
		return "enum: " + value;
	}
}

export class RendererImpl implements Renderer {
	prefix:string;
	suffix:string;
	testStuff:prezi.graphics.text.TestStuff<string, string>;

	constructor(prefix:string, suffix:string) {
		this.prefix = prefix;
		this.suffix = suffix;
		this.testStuff = prezi.graphics.text.Layout.createTestStuff();
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
