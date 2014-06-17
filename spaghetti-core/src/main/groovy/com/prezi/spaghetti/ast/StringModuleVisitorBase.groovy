package com.prezi.spaghetti.ast

class StringModuleVisitorBase extends ModuleVisitorBase<String> {
	@Override
	protected String defaultResult() {
		return ""
	}

	@Override
	String aggregateResult(String aggregate, String nextResult) {
		return aggregate + nextResult
	}
}
