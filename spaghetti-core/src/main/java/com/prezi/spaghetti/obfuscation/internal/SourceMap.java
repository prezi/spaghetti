package com.prezi.spaghetti.obfuscation.internal;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.debugging.sourcemap.SourceMapConsumerV3;
import com.google.debugging.sourcemap.SourceMapParseException;
import com.google.debugging.sourcemap.proto.Mapping;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SourceMap {
	public static String compose(final String mapAtoB, final String mapBtoC, String mapAtoCName, String nodeSourceMapRoot) throws IOException, InterruptedException {
		String nodeJsSource = "\nvar sourceMap = require('source-map');\n" +
				"\n" +
				"var mapBtoC = " + mapBtoC + ";\n" +
				"var mapAtoB = " + mapAtoB + ";\n" +
				"\n" +
				"var map = sourceMap.SourceMapGenerator.fromSourceMap(new sourceMap.SourceMapConsumer(mapBtoC));\n" +
				"map.applySourceMap(new sourceMap.SourceMapConsumer(mapAtoB), mapBtoC.sources[0]);\n" +
				"var mapAtoC = map.toJSON();\n" +
				"mapAtoC.file = '" + mapAtoCName + "';\n" +
				"console.log(JSON.stringify(mapAtoC));\n";

		File nodeJsFile = File.createTempFile("nodejs_map", ".js");
		Files.write(nodeJsSource, nodeJsFile, Charsets.UTF_8);

		ProcessBuilder processBuilder = new ProcessBuilder("node", nodeJsFile.toString());
		processBuilder.redirectErrorStream(true);
		// if user gives a node_modules path use that, otherwise just assume it's there (e.g. global install)
		if (nodeSourceMapRoot != null) {
			processBuilder.environment().put("NODE_PATH", nodeSourceMapRoot);
		}

		Process process = processBuilder.start();
		String mapAtoC;
		InputStreamReader reader = new InputStreamReader(process.getInputStream(), Charsets.UTF_8);
		boolean threw = true;
		try {
			mapAtoC = CharStreams.toString(reader);
			threw = false;
		} finally {
			Closeables.close(reader, threw);
		}

		process.waitFor();
		if (process.exitValue() != 0) {
			System.err.println(mapAtoC);
			throw new RuntimeException("Source map composition failed with exit code " + process.exitValue());
		}

		// adding lineCount so that Closure stays happy
		return addLineCount(mapBtoC, mapAtoC);
	}

	@SuppressWarnings("unchecked")
	private static String addLineCount(String mapBtoC, String mapAtoC) {
		Gson gson = new Gson();
		Map mAC = gson.fromJson(mapAtoC, Map.class);
		Map mBC = gson.fromJson(mapBtoC, Map.class);
		mAC.put("lineCount", mBC.get("lineCount"));
		return gson.toJson(mAC);
	}

	/**
	 * Takes a sourcemap A and gives back a sourcemap B in which paths
	 * in the "sources" field are the same as in A only relative to
	 * 'root'. If this is not possible it gives back the original
	 * path.
	 */
	@SuppressWarnings("unchecked")
	public static String relativizePaths(String sourceMap, final URI root) {
		Gson gson = new Gson();
		Map mapJSON = gson.fromJson(sourceMap, Map.class);
		Collection<String> sources = (List) mapJSON.get("sources");
		sources = Collections2.transform(sources, new Function<String, String>() {
			@Override
			public String apply(String input) {
				if (input.startsWith("file://")) {
					input = input.substring(0, "file://".length());
				}
				return root.relativize(URI.create(input)).toString();
			}
		});
		mapJSON.put("sources", sources);
		return gson.toJson(mapJSON);
	}

	/**
	 * Does a reverse lookup of the 'lineNumber' in the
	 * 'sourceMap'. Returns the original line number and appends the
	 * original source filename to 'retSource'
	 */
	public static int reverseMapping(String sourceMap, int lineNumber, Appendable retSource) throws SourceMapParseException, IOException {
		SourceMapConsumerV3 smV3 = new SourceMapConsumerV3();
		smV3.parse(sourceMap);
		// this column number ensures we get a result if there is one
		Mapping.OriginalMapping mapping = smV3.getMappingForLine(lineNumber, Integer.MAX_VALUE);
		retSource.append(mapping.getOriginalFile());
		return mapping.getLineNumber();
	}
}
