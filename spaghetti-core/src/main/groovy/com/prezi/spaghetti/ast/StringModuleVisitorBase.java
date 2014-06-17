package com.prezi.spaghetti.ast;

public class StringModuleVisitorBase extends ModuleVisitorBase<String> {
	@Override
	protected String defaultResult() {
		return "";
	}

	@Override
	public String aggregateResult(String aggregate, String nextResult) {
		return aggregate + nextResult;
	}
}
