package com.prezi.spaghetti.ast.internal

import com.prezi.spaghetti.ast.ArrayedTypeReference

/**
 * Created by lptr on 31/05/14.
 */
abstract class AbstractArrayedTypeReference extends AbstractNode implements ArrayedTypeReference {
	final int arrayDimensions

	AbstractArrayedTypeReference(int arrayDimensions) {
		this.arrayDimensions = arrayDimensions
	}
}
