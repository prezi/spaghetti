define(function() { var __module;
(function () { "use strict";
var HxOverrides = function() { }
HxOverrides.substr = function(s,pos,len) {
	if(pos != null && pos != 0 && len != null && len < 0) return "";
	if(len == null) len = s.length;
	if(pos < 0) {
		pos = s.length + pos;
		if(pos < 0) pos = 0;
	} else if(len < 0) len = s.length + len - pos;
	return s.substr(pos,len);
}
var prezi = {}
prezi.graphics = {}
prezi.graphics.text = {}
prezi.graphics.text.Layout = function() { }
prezi.graphics.text.LayoutImpl = function() {
};
prezi.graphics.text.LayoutImpl.__interfaces__ = [prezi.graphics.text.Layout];
prezi.graphics.text.LayoutImpl.prototype = {
	createText: function(numberOfParagraphs) {
		return new prezi.graphics.text.TextImpl();
	}
}
prezi.graphics.text.Text = function() { }
prezi.graphics.text.TextImpl = function() {
	this.text = "";
};
prezi.graphics.text.TextImpl.__interfaces__ = [prezi.graphics.text.Text];
prezi.graphics.text.TextImpl.prototype = {
	'delete': function(offset,end) {
		this.text = HxOverrides.substr(this.text,0,offset) + HxOverrides.substr(this.text,end,null);
	}
	,insert: function(offset,textToInsert) {
		this.text = HxOverrides.substr(this.text,0,offset) + textToInsert + HxOverrides.substr(this.text,offset,null);
	}
	,getRawText: function() {
		return this.text;
	}
}
prezi.graphics.text.LayoutInit = function() { }
prezi.graphics.text.LayoutInit.main = function() {
	__module = new prezi.graphics.text.LayoutImpl();
}
prezi.graphics.text.LayoutInit.main();
})();
return __module;});