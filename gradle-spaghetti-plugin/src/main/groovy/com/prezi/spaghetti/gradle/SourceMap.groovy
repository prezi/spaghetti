package com.prezi.spaghetti.gradle;

import groovy.json.*;
import com.google.debugging.sourcemap.*;

// A wrapper class for hacking together handling of sourcemaps
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

		if (process.exitValue() != 0) {
			throw new RuntimeException("Source map composition failed with exit code " + process.exitValue());
		}

		def mapAtoC = mapAtoCBuilder.toString();

		// adding lineCount so that Closure stays happy
		def slurper = new JsonSlurper();
		def mAC = slurper.parseText(mapAtoC);
		def mBC = slurper.parseText(mapBtoC);
		mAC.lineCount = mBC.lineCount;

		return new JsonBuilder(mAC).toString();
	}

	/**
	 * Takes a sourcemap A and gives back a sourcemap B in which paths
	 * in the "sources" field are the same as in A only relative to
	 * 'root'. If this is not possible it gives back the original
	 * path.
	 */
	public static String relativizePaths(String sourceMap, URI root) {
		def slurper = new JsonSlurper();
		def mapJSON = slurper.parseText(sourceMap);

		mapJSON.sources = mapJSON.sources.collect{root.relativize(new URI(it - ~/^file:\/\//)).toString()};

		return new JsonBuilder(mapJSON).toString();
	}

	/**
	 * Does a reverse lookup of the 'lineNumber' in the
	 * 'sourceMap'. Returns the original line number and appends the
	 * original source filename to 'retSource'
	 */
	public static int reverseMapping(String sourceMap, int lineNumber, Appendable retSource) {
		def smV3 = new SourceMapConsumerV3();
		smV3.parse(sourceMap);
		// this column number ensures we get a result if there is one
		def mapping = smV3.getMappingForLine(lineNumber, Integer.MAX_VALUE);
		retSource.append(mapping.originalFile);
		return mapping.lineNumber;
	}
}
