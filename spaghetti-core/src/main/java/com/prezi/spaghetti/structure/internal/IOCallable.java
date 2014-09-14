package com.prezi.spaghetti.structure.internal;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * A callable that can throw an {@link java.io.IOException}.
 */
public interface IOCallable<T> extends Callable<T> {
	/**
	 * Computes a result, or throws an exception if unable to do so.
	 */
	T call() throws IOException;
}
