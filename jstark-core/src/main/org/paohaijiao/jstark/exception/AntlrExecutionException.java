package org.paohaijiao.jstark.exception;

import lombok.Data;

import java.util.List;
@Data
public class AntlrExecutionException  extends RuntimeException{
    private final List<AntlrError> errors;
}
