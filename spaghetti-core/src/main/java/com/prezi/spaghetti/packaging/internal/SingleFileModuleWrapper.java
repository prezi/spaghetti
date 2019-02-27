package com.prezi.spaghetti.packaging.internal;

import com.prezi.spaghetti.packaging.ModuleWrapperParameters;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.prezi.spaghetti.generator.ReservedWords.MODULE;

public class SingleFileModuleWrapper extends AbstractModuleWrapper {
	@Override
	public String wrap(ModuleWrapperParameters params) throws IOException {

		StringBuilder result = new StringBuilder();
		result.append("function(){");
		result.append("var baseUrl=__dirname;");
		result.append("return(");
		wrapModuleObject(
				result,
				params,
				true);
		result.append(").apply({},arguments);");
		result.append("}");
		return result.toString();
	}

	@Override
	protected void makeMainModuleSetup(StringBuilder result, String mainModule, boolean execute) {
		result.append("var mainModule=modules[\"").append(mainModule).append("\"][\"").append(MODULE).append("\"];");
		if (execute) {
			result.append("mainModule[\"main\"]();");
		}
	}
}
