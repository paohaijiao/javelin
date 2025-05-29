package org.paohaijiao.jstark.antlr;

import org.paohaijiao.jstark.exception.AntlrError;

public interface AntlrErrorListener {
    void onError(AntlrError error);
}
