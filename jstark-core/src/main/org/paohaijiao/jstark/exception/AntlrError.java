package org.paohaijiao.jstark.exception;

import lombok.Data;

import java.util.List;

@Data
public class AntlrError {
    private final String message;
    private final int line;
    private final int charPosition;
    private final List<String> ruleStack;
}
