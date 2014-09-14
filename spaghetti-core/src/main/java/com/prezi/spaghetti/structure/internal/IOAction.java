package com.prezi.spaghetti.structure.internal;

import java.io.IOException;

/**
 * An action that can throw an {@link java.io.IOException}.
 */
public interface IOAction<T> {
	/**
	 * Execute the I/O action on a given object.
	 */
	void execute(T t) throws IOException;
}
