package com.prezi.spaghetti.ast;

public interface TypeReference extends AstNode {
	int getArrayDimensions();
	TypeReference withAdditionalArrayDimensions(int extraDimensions);
}
