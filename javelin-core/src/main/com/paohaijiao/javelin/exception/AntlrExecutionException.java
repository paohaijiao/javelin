package com.paohaijiao.javelin.exception;

import lombok.Data;

import java.util.List;
@Data
public class AntlrExecutionException  extends RuntimeException{
    private final List<AntlrError> errors;
}
