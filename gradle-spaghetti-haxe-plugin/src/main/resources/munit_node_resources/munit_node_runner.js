#!/usr/bin/env node

var jsdom = require("jsdom");
var Canvas = require('canvas');
var describe = require("mocha").describe;
global.window = jsdom.jsdom().createWindow();
global.document = global.window.document;
global.$ = require('jquery');

navigator = {userAgent: {match: function(){}}};

require("./nodejsTest_test.js");