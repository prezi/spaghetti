package com.prezi.spaghetti.javascript;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.generator.AbstractHeaderGenerator;
import com.prezi.spaghetti.generator.GeneratorParameters;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class JavaScriptHeaderGenerator extends AbstractHeaderGenerator {
	public JavaScriptHeaderGenerator() {
		super("js");
	}

	@Override
	public void generateHeaders(GeneratorParameters params, File outputDirectory) throws IOException {
		String header = params.getHeader();
		String contents = "";
		for (ModuleNode moduleNode : params.getModuleConfiguration().getAllModules()) {
			// TODO Generate the package structure once per module, and put everything in the module under that single structure
			contents += moduleNode.accept(new JavaScriptConstGeneratorVisitor());
			contents += moduleNode.accept(new JavaScriptEnumGeneratorVisitor());
			createSourceFile(header, moduleNode.getAlias(), outputDirectory, contents);
		}
	}

	private static File createSourceFile(String header, String name, File outputDirectory, String contents) throws IOException {
		File file = new File(outputDirectory, name + ".js");
		FileUtils.deleteQuietly(file);
		CharSink out = Files.asCharSink(file, Charsets.UTF_8);
		out.write(
				"/*\n"
				+ " * " + header + "\n"
				+ " */\n"
				+ contents
		);
		return file;
	}
}
