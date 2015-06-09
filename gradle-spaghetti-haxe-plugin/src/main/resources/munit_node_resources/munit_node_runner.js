#!/usr/bin/env node

var jsdom = require("jsdom");
var fs = require('fs');

global.$ = require('jquery');
global.window = jsdom.jsdom().createWindow();
global.alert = function(){};

for(var p in global.window)
{

    if(!global[p])
        global[p] = global.window[p];
}


var pprt = (function(){
	var indentLevel = 0;
	var isFresh = true;
	var freshLine = function()
	{
		if(!isFresh)
		{
			process.stdout.write("\n");
			isFresh = true;
		}
	}

	var writeLine = function(x)
	{
		write(x);
		freshLine();
	}

	var write = function(x)
	{
		var first = true;
		x.split("\n").map(function(line){
			if(!first)
				process.stdout.write("\n");
			first = false;
			if(isFresh || !first)
			{
				for(i=0;i<indentLevel;i++)
					process.stdout.write("    ");
			}
			process.stdout.write(line);
		
		});
		isFresh = false;

	}

	var indent = function()
	{
		indentLevel++;		
	}

	var unindent = function()
	{
		indentLevel--;
	}

	return {
		freshLine: freshLine,
		writeLine: writeLine,
		write: write,
		indent: indent,
		unindent: unindent,
	}
})();

global.addToQueue = (function ()
{
	var oldLog = console.log;
//	console.log = function(){}

	var trace = [];

	function unhtml(st)
	{
		st = st.replace(/&nbsp;/g, ' ');
		st = st.replace(/<br\/>/g, '\n');
		return st;
	}

	function flushTrace()
	{
		pprt.indent();
		while(trace.length)
			pprt.writeLine(trace.shift());
		pprt.unindent();
	}

	
	function addToQueue(fnc,arg1,arg2,arg3)
	{
		arg1 = unhtml(arg1);
		if(fnc == "updateTestSummary")
		{
			pprt.write(unhtml(arg1));
		}
		else if(fnc == "setTestClassResult")	
		{

			switch(arg1)
			{
				case "0":
					pprt.writeLine("PASSED");
					break;
				case "1":
					pprt.writeLine("FAILED");
					break;
				case "2":
					pprt.writeLine("ERROR");
					break;
				case "3":
					//color = COLOR_WARNING;// yellow passed but not covered
					pprt.writeLine("PASSED");
					break;
				default: 
					break;
			}

			flushTrace();

		}
		else if(fnc == "addTestError")
		{
			trace.push(arg1);

		}
		else if(fnc == "createTestClass")	
		{

		}
		else if(fnc == "addTestIgnore")	
		{

		}
		else if(fnc == "munitTrace")
		{
			trace.push(arg1);
		
		}
		else if(fnc == "printSummary")
		{
			pprt.freshLine("");
			oldLog(arg1);	
		}
		else if(fnc == "setResult")
		{
			flushTrace();
		}
		else
		{
			pprt.freshLine();
			oldLog("XXXXX", arguments);
			
		}
			
	}

	return addToQueue;
})();


global.testComplete = function(resultXml, successful){

    function mkdir(path, root) {

        var dirs = path.split('/'), dir = dirs.shift(), root = (root||'')+dir+'/';

        try { fs.mkdirSync(root); }
        catch (e) {
            //dir wasn't made, something went wrong
            if(!fs.statSync(root).isDirectory()) throw new Error(e);
        }

        return !dirs.length||mkdir(dirs.join('/'), root);
    }


    mkdir('report/test/junit/js/xml');
    fs.writeFileSync("report/test/junit/js/xml/report.xml", resultXml);

    process.exit(successful ? 0 : 1);

}


navigator = {userAgent: {match: function(){}}};


require("./nodejsTest_test.js");