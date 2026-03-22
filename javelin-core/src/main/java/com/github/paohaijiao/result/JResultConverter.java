package com.github.paohaijiao.result;

import java.io.IOException;

public interface JResultConverter<T> {


    T convert(JResult response) throws IOException;
}
