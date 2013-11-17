require(["Layout"], function() {
var __modules = arguments;
(function () { "use strict";
var prezi = {}
prezi.graphics = {}
prezi.graphics.text = {}
prezi.graphics.text.Layout = function() { }
prezi.graphics.text.Text = function() { }
prezi.test = {}
prezi.test.client = {}
prezi.test.client.Client = function() { }
prezi.test.client.Client.main = function() {
	var layout = prezi.test.client.Modules.modules[0];
	var text = layout.createText(2);
	text.insert(0,"World");
	text.insert(0,"Hello ");
	console.log(text.getRawText());
}
prezi.test.client.Modules = function() { }
prezi.test.client.Modules.getLayout = function() {
	return prezi.test.client.Modules.modules[0];
}
prezi.test.client.Modules.modules = __modules;
prezi.test.client.Client.main();
})();
});
