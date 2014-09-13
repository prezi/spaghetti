define(["require","prezi.graphics.core"],function(){var module=(function(dependencies){return function(init){return init.call({},(function(){var moduleUrl=dependencies[0]["toUrl"]("prezi.graphics.text.js");var baseUrl=moduleUrl.substr(0,moduleUrl.lastIndexOf("/"));return{getSpaghettiVersion:function(){return "2.0-rc6-SNAPSHOT";},getName:function(){return "prezi.graphics.text";},getVersion:function(){return "0.1";},getResourceUrl:function(resource){if(resource.substr(0,1)!="/"){resource="/"+resource;}return baseUrl+resource;},"dependencies":{"require":dependencies[0],"prezi.graphics.core":dependencies[1]}};})());};})(arguments);return{"module":module(function(Spaghetti) {
// Haxe expects either window or exports to be present
var exports = exports || {};
var __haxeModule;
(function () { "use strict";
var HxOverrides = function() { };
HxOverrides.__name__ = true;
HxOverrides.substr = function(s,pos,len) {
	if(pos != null && pos != 0 && len != null && len < 0) return "";
	if(len == null) len = s.length;
	if(pos < 0) {
		pos = s.length + pos;
		if(pos < 0) pos = 0;
	} else if(len < 0) len = s.length + len - pos;
	return s.substr(pos,len);
};
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
prezi.graphics.text.AbstractTextStub = function() { };
prezi.graphics.text.AbstractTextStub.__name__ = true;
prezi.graphics.text.AbstractTextStub.__interfaces__ = [prezi.graphics.text.AbstractText];
prezi.graphics.text.AbstractTextStub.prototype = {
	insert: function(offset,text,withStyles) {
	}
	,'delete': function(offset,end) {
	}
};
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
prezi.graphics.text.Dummy = function() { };
prezi.graphics.text.Dummy.__name__ = true;
prezi.graphics.text.DummyStub = function() { };
prezi.graphics.text.DummyStub.__name__ = true;
prezi.graphics.text.DummyStub.__interfaces__ = [prezi.graphics.text.Dummy];
prezi.graphics.text.DummyStub.prototype = {
	getValue: function() {
		return 0;
	}
};
prezi.graphics.text.Layout = function() { };
prezi.graphics.text.Layout.__name__ = true;
prezi.graphics.text.Layout.createText = function(text) {
	if(text == null) text = "";
	console.log("Layout name: " + Spaghetti.getName());
	console.log("Static call: " + prezi.graphics.core.Core.module.giveMeANumber());
	var result = new prezi.graphics.text.TextImpl();
	result.insert(0,text,[]);
	return result;
};
prezi.graphics.text.Layout.createTestStuff = function() {
	return new prezi.graphics.text.TestStuffImpl();
};
prezi.graphics.text.Layout.createTestStuffWithStringKey = function() {
	return new prezi.graphics.text.TestStuffImpl();
};
prezi.graphics.text.Layout.getResource = function() {
	return Spaghetti.getResourceUrl("sample.txt");
};
prezi.graphics.text.TestStuff = function() { };
prezi.graphics.text.TestStuff.__name__ = true;
prezi.graphics.text.TestStuff2 = function() { };
prezi.graphics.text.TestStuff2.__name__ = true;
prezi.graphics.text.TestStuff2.__interfaces__ = [prezi.graphics.text.Dummy,prezi.graphics.text.TestStuff];
prezi.graphics.text.TestStuff2Stub = function() { };
prezi.graphics.text.TestStuff2Stub.__name__ = true;
prezi.graphics.text.TestStuff2Stub.__interfaces__ = [prezi.graphics.text.TestStuff2];
prezi.graphics.text.TestStuff2Stub.prototype = {
	readValue: function(object,name,defaultValue) {
		return null;
	}
	,registerCallback: function(callback) {
	}
	,doVoidCallback: function(callback) {
		return 0;
	}
	,doAsync: function(callback,converter) {
		return 0;
	}
	,doSomething: function(pre,text,post) {
		return null;
	}
	,drawSomething: function(canvas) {
	}
	,getValue: function() {
		return 0;
	}
};
prezi.graphics.text.TestStuffImpl = function() {
};
prezi.graphics.text.TestStuffImpl.__name__ = true;
prezi.graphics.text.TestStuffImpl.__interfaces__ = [prezi.graphics.text.TestStuff];
prezi.graphics.text.TestStuffImpl.prototype = {
	registerCallback: function(callback) {
		callback("Calling callback");
	}
	,doVoidCallback: function(callback) {
		return callback();
	}
	,doAsync: function(callback,converter) {
		callback("Calling async callback",converter);
		return 12;
	}
	,doSomething: function(pre,text,post) {
		return "" + Std.string(pre) + "-" + text + "-" + Std.string(post);
	}
	,drawSomething: function(canvas) {
		canvas.getContext("2d").fillRect(10,10,100,50);
	}
};
prezi.graphics.text.TestStuffStub = function() { };
prezi.graphics.text.TestStuffStub.__name__ = true;
prezi.graphics.text.TestStuffStub.__interfaces__ = [prezi.graphics.text.TestStuff];
prezi.graphics.text.TestStuffStub.prototype = {
	registerCallback: function(callback) {
	}
	,doVoidCallback: function(callback) {
		return 0;
	}
	,doAsync: function(callback,converter) {
		return 0;
	}
	,doSomething: function(pre,text,post) {
		return null;
	}
	,drawSomething: function(canvas) {
	}
};
prezi.graphics.text.Text = function() { };
prezi.graphics.text.Text.__name__ = true;
prezi.graphics.text.Text.__interfaces__ = [prezi.graphics.text.AbstractText];
prezi.graphics.text.TextImpl = function() {
	this.text = "";
};
prezi.graphics.text.TextImpl.__name__ = true;
prezi.graphics.text.TextImpl.__interfaces__ = [prezi.graphics.text.Text];
prezi.graphics.text.TextImpl.prototype = {
	getRawText: function() {
		return this.text;
	}
	,insert: function(offset,textToInsert,withStyles) {
		var _g = 0;
		while(_g < withStyles.length) {
			var style = withStyles[_g];
			++_g;
			console.log("Got style: " + prezi.graphics.text._CharacterStyleType.CharacterStyleType_Impl_._names[style.type] + " = " + Std.string(style.value));
		}
		this.text = HxOverrides.substr(this.text,0,offset) + textToInsert + HxOverrides.substr(this.text,offset,null);
	}
	,'delete': function(offset,end) {
		this.text = HxOverrides.substr(this.text,0,offset) + HxOverrides.substr(this.text,end,null);
	}
};
prezi.graphics.text.TextStub = function() { };
prezi.graphics.text.TextStub.__name__ = true;
prezi.graphics.text.TextStub.__interfaces__ = [prezi.graphics.text.Text];
prezi.graphics.text.TextStub.prototype = {
	getRawText: function() {
		return null;
	}
	,insert: function(offset,text,withStyles) {
	}
	,'delete': function(offset,end) {
	}
};
prezi.graphics.text.Values = function() { };
prezi.graphics.text.Values.__name__ = true;
prezi.graphics.text.__LayoutProxy = function() {
};
prezi.graphics.text.__LayoutProxy.__name__ = true;
prezi.graphics.text.__LayoutProxy.prototype = {
	createText: function(text) {
		return prezi.graphics.text.Layout.createText(text);
	}
	,createTestStuff: function() {
		return prezi.graphics.text.Layout.createTestStuff();
	}
	,getResource: function() {
		return prezi.graphics.text.Layout.getResource();
	}
	,createTestStuffWithStringKey: function() {
		return prezi.graphics.text.Layout.createTestStuffWithStringKey();
	}
};
prezi.graphics.text.__LayoutInit = function() { };
prezi.graphics.text.__LayoutInit.__name__ = true;
prezi.graphics.text.__LayoutInit.delayedInit = function() {
	__haxeModule = new prezi.graphics.text.__LayoutProxy();
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
prezi.graphics.text.Values.HELLO = 12;
prezi.graphics.text.Values.HI = "Hello";
prezi.graphics.text.__LayoutInit.delayedInitFinished = prezi.graphics.text.__LayoutInit.delayedInit();
})();

//# sourceMappingURL=compiled.js.map
return __haxeModule;

})
,"version":"0.1","spaghettiVersion":"2.0-rc6-SNAPSHOT"};});