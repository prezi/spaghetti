package com.prezi.spaghetti;

// For composing source-maps; requires "npm install source-map"
class SourceMap {

	// returns the source map from A to C. ugly.
	public static String compose(String mapAtoB, String mapBtoC, String mapAtoCName) {
		String nodeJsSource =
"""
var sourceMap = require('source-map');

var mapBtoC = ${mapBtoC};
var mapAtoB = ${mapAtoB};

var map = sourceMap.SourceMapGenerator.fromSourceMap(new sourceMap.SourceMapConsumer(mapBtoC));
map.applySourceMap(new sourceMap.SourceMapConsumer(mapAtoB), mapBtoC.sources[0]);
var mapAtoC = map.toJSON();
mapAtoC.file = '${mapAtoCName}';
console.log(JSON.stringify(mapAtoC));
""";

			def nodeJsFile = File.createTempFile("nodejs_map", ".js");

			nodeJsFile << nodeJsSource;

			def mapAtoCBuilder = new StringBuilder();
			def cmdLine =  ["node", nodeJsFile];
			def process = cmdLine.execute();
			process.waitForProcessOutput(mapAtoCBuilder, System.err);

			// nodeJsFile.delete();

			if (process.exitValue() != 0) {
				throw new RuntimeException("Source map composition failed with exit code " + process.exitValue());
			}

			return mapAtoCBuilder.toString();
	}

	public static String composeAtoZ(String[] maps) {

		if (maps.length == 0) {
			return "";
		} else {
			def tmp = maps[0];

			for (int i = 1; i < maps.length; i++) {
				tmp = compose(tmp, maps[i]);
			}
			return tmp;
		}
	}

}
