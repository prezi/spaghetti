package com.prezi.spaghetti.javascript;

import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;
import com.prezi.spaghetti.ast.ModuleNode;
import com.prezi.spaghetti.config.ModuleConfiguration;
import com.prezi.spaghetti.generator.AbstractGenerator;
import com.prezi.spaghetti.generator.internal.GeneratorUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class JavaScriptGenerator extends AbstractGenerator {
	public JavaScriptGenerator(ModuleConfiguration config) {
		super(config);
	}

	@Override
	public void generateHeaders(File outputDirectory) throws IOException {
		String contents = "";
		for (ModuleNode moduleNode : config.getAllModules()) {
			// TODO Generate the package structure once per module, and put everything in the module under that single structure
			contents += moduleNode.accept(new JavaScriptConstGeneratorVisitor());
			contents += moduleNode.accept(new JavaScriptEnumGeneratorVisitor());
			createSourceFile(moduleNode.getAlias(), outputDirectory, contents);
		}
	}

	@Override
	protected String processModuleJavaScriptInternal(ModuleNode module, String javaScript) {
		return javaScript;
	}

	public static File createSourceFile(String name, File outputDirectory, String contents) throws IOException {
		File file = new File(outputDirectory, name + ".js");
		FileUtils.deleteQuietly(file);
		CharSink out = Files.asCharSink(file, Charsets.UTF_8);
		out.write(
				"/*\n"
				+ " * " + GeneratorUtils.createHeaderComment() + "\n"
				+ " */\n"
				+ contents
		);
		return file;
	}
}
