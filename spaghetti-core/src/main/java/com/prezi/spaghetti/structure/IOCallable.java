package com.prezi.spaghetti.structure;

import java.io.IOException;

public interface IOCallable<T> {
	T call() throws IOException;
}
