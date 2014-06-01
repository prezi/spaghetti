package com.prezi.spaghetti.ast

/**
 * Created by lptr on 30/05/14.
 */
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
