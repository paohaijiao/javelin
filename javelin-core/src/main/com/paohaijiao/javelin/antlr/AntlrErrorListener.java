package com.paohaijiao.javelin.antlr;

import com.paohaijiao.javelin.exception.AntlrError;

public interface AntlrErrorListener {
    void onError(AntlrError error);
}
