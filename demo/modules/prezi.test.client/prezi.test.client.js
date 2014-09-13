define(["require","prezi.graphics.core","prezi.graphics.text","prezi.graphics.text.render"],function(){var module=(function(dependencies){return function(init){return init.call({},(function(){var moduleUrl=dependencies[0]["toUrl"]("prezi.test.client.js");var baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf("/"));return{getSpaghettiVersion:function(){return "2.0-rc6-SNAPSHOT";},getName:function(){return "prezi.test.client";},getVersion:function(){return "0.1";},getResourceUrl:function(resource){if(resource.substr(0,1)!="/"){resource="/"+resource;}return baseUrl+resource;},"dependencies":{"require":dependencies[0],"prezi.graphics.core":dependencies[1],"prezi.graphics.text":dependencies[2],"prezi.graphics.text.render":dependencies[3]}};})());};})(arguments);return{"module":module(function(Spaghetti) {
// Haxe expects either window or exports to be present
var exports = exports || {};
var __haxeModule;
(function () { "use strict";
var Std = function() { };
Std.__name__ = true;
Std.string = function(s) {
	return js.Boot.__string_rec(s,"");
};
var js = {};
js.Boot = function() { };
js.Boot.__name__ = true;
js.Boot.__string_rec = function(o,s) {
	if(o == null) return "null";
	if(s.length >= 5) return "<...>";
	var t = typeof(o);
	if(t == "function" && (o.__name__ || o.__ename__)) t = "object";
	switch(t) {
	case "object":
		if(o instanceof Array) {
			if(o.__enum__) {
				if(o.length == 2) return o[0];
				var str = o[0] + "(";
				s += "\t";
				var _g1 = 2;
				var _g = o.length;
				while(_g1 < _g) {
					var i = _g1++;
					if(i != 2) str += "," + js.Boot.__string_rec(o[i],s); else str += js.Boot.__string_rec(o[i],s);
				}
				return str + ")";
			}
			var l = o.length;
			var i1;
			var str1 = "[";
			s += "\t";
			var _g2 = 0;
			while(_g2 < l) {
				var i2 = _g2++;
				str1 += (i2 > 0?",":"") + js.Boot.__string_rec(o[i2],s);
			}
			str1 += "]";
			return str1;
		}
		var tostr;
		try {
			tostr = o.toString;
		} catch( e ) {
			return "???";
		}
		if(tostr != null && tostr != Object.toString) {
			var s2 = o.toString();
			if(s2 != "[object Object]") return s2;
		}
		var k = null;
		var str2 = "{\n";
		s += "\t";
		var hasp = o.hasOwnProperty != null;
		for( var k in o ) {
		if(hasp && !o.hasOwnProperty(k)) {
			continue;
		}
		if(k == "prototype" || k == "__class__" || k == "__super__" || k == "__interfaces__" || k == "__properties__") {
			continue;
		}
		if(str2.length != 2) str2 += ", \n";
		str2 += s + k + " : " + js.Boot.__string_rec(o[k],s);
		}
		s = s.substring(1);
		str2 += "\n" + s + "}";
		return str2;
	case "function":
		return "<function>";
	case "string":
		return o;
	default:
		return String(o);
	}
};
var prezi = {};
prezi.graphics = {};
prezi.graphics.core = {};
prezi.graphics.core.Core = function() { };
prezi.graphics.core.Core.__name__ = true;
prezi.graphics.text = {};
prezi.graphics.text.AbstractText = function() { };
prezi.graphics.text.AbstractText.__name__ = true;
prezi.graphics.text._CharacterStyleType = {};
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_ = function() { };
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.__name__ = true;
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_._new = function(value) {
	return value;
};
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.value = function(this1) {
	return this1;
};
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.fromValue = function(value) {
	if(value < 0 || value >= prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_._values.length) throw Error("Invalid value for CharacterStyleType: " + value);
	var result = prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_._values[value];
	return result;
};
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.$name = function(this1) {
	return prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_._names[this1];
};
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.valueOf = function(name) {
	var value = prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_._namesToValues[name];
	if(value == null) throw Error("Invalid name for CharacterStyleType: " + name);
	return value;
};
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.values = function() {
	return prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_._values.slice();
};
prezi.graphics.text.Layout = function() { };
prezi.graphics.text.Layout.__name__ = true;
prezi.graphics.text.TestStuff = function() { };
prezi.graphics.text.TestStuff.__name__ = true;
prezi.graphics.text.Text = function() { };
prezi.graphics.text.Text.__name__ = true;
prezi.graphics.text.Text.__interfaces__ = [prezi.graphics.text.AbstractText];
prezi.graphics.text.Values = function() { };
prezi.graphics.text.Values.__name__ = true;
prezi.graphics.text.render = {};
prezi.graphics.text.render.RenderModule = function() { };
prezi.graphics.text.render.RenderModule.__name__ = true;
prezi.graphics.text.render.Renderer = function() { };
prezi.graphics.text.render.Renderer.__name__ = true;
prezi.test = {};
prezi.test.client = {};
prezi.test.client.Client = function() { };
prezi.test.client.Client.__name__ = true;
prezi.test.client.Client.main = function() {
	console.log("App name: " + Spaghetti.getName());
	console.log("App version: " + Spaghetti.getVersion());
	console.log("App built by Spaghetti version " + Spaghetti.getSpaghettiVersion());
	var text = prezi.graphics.text.Layout.module.createText(null);
	var style = { type : prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.FONT_WEIGHT, value : "bold"};
	style.value = "normal";
	text.insert(0,"World",[style]);
	text.insert(0,"Hello",[]);
	var renderer = prezi.graphics.text.render.RenderModule.module.createRenderer("Text rendered with render module: [","]");
	console.log(renderer.render(text));
	var testStuff = prezi.graphics.text.Layout.module.createTestStuff();
	testStuff.registerCallback(function(message) {
		console.log("Received callback message: " + message);
	});
	console.log("TestStuff.doSomething(): " + testStuff.doSomething("pre","text","pos"));
	if(typeof(document) != "undefined") {
		var canvas = document.getElementById('canvas');
		testStuff.drawSomething(canvas);
	}
	var result = testStuff.doAsync(prezi.test.client.Client.callback,function(value) {
		return Std.string(value + 1);
	});
	console.log("doAync() returned: " + result);
	console.log("Haxe resource: " + prezi.graphics.text.Layout.module.getResource());
	console.log("TypeScript resource: " + prezi.graphics.text.render.RenderModule.module.getResource());
};
prezi.test.client.Client.callback = function(name,converter) {
	console.log("Name: " + name + ", converting 42: " + converter(42));
};
prezi.test.client.__ClientProxy = function() {
};
prezi.test.client.__ClientProxy.__name__ = true;
prezi.test.client.__ClientProxy.prototype = {
	main: function() {
		prezi.test.client.Client.main();
	}
};
prezi.test.client.__ClientInit = function() { };
prezi.test.client.__ClientInit.__name__ = true;
prezi.test.client.__ClientInit.delayedInit = function() {
	__haxeModule = new prezi.test.client.__ClientProxy();
	return true;
};
String.__name__ = true;
Array.__name__ = true;
prezi.graphics.core.Core.module = Spaghetti["dependencies"]["prezi.graphics.core"]["module"];
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.COLOR = 0;
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.FONT_FAMILY = 1;
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.FONT_STYLE = 2;
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.FONT_STRETCH = 3;
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.FONT_WEIGHT = 4;
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.UNDERLINE = 5;
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.URL = 6;
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_._values = [prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.COLOR,prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.FONT_FAMILY,prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.FONT_STYLE,prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.FONT_STRETCH,prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.FONT_WEIGHT,prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.UNDERLINE,prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.URL];
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_._names = ["COLOR","FONT_FAMILY","FONT_STYLE","FONT_STRETCH","FONT_WEIGHT","UNDERLINE","URL"];
prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_._namesToValues = { COLOR : prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.COLOR, FONT_FAMILY : prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.FONT_FAMILY, FONT_STYLE : prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.FONT_STYLE, FONT_STRETCH : prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.FONT_STRETCH, FONT_WEIGHT : prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.FONT_WEIGHT, UNDERLINE : prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.UNDERLINE, URL : prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_.URL};
prezi.graphics.text.Layout.module = Spaghetti["dependencies"]["prezi.graphics.text"]["module"];
prezi.graphics.text.Values.HELLO = 12;
prezi.graphics.text.Values.HI = "Hello";
prezi.graphics.text.render.RenderModule.module = Spaghetti["dependencies"]["prezi.graphics.text.render"]["module"];
prezi.test.client.__ClientInit.delayedInitFinished = prezi.test.client.__ClientInit.delayedInit();
})();

return __haxeModule;

})
,"version":"0.1","spaghettiVersion":"2.0-rc6-SNAPSHOT"};});