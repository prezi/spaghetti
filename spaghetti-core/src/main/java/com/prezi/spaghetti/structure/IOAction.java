package com.prezi.spaghetti.structure;

import java.io.IOException;

public interface IOAction<T> {
    void execute(T t) throws IOException;
}
