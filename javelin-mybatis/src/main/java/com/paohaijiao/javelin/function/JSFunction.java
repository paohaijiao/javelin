package com.paohaijiao.javelin.function;

import java.io.Serializable;
import java.util.function.Function;

public interface JSFunction<T, R> extends Function<T, R>, Serializable {
}
