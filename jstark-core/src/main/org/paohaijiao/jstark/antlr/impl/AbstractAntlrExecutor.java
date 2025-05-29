package org.paohaijiao.jstark.antlr.impl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.paohaijiao.jstark.antlr.AntlrErrorListener;
import org.paohaijiao.jstark.antlr.AntlrExecutor;
import org.paohaijiao.jstark.exception.AntlrError;
import org.paohaijiao.jstark.exception.AntlrExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class AbstractAntlrExecutor <I, O> implements AntlrExecutor<I, O> {
    protected final List<AntlrErrorListener> errorListeners = new ArrayList<>();

    @Override
    public void addErrorListener(AntlrErrorListener listener) {
        errorListeners.add(listener);
    }

    @Override
    public void removeErrorListener(AntlrErrorListener listener) {
        errorListeners.remove(listener);
    }

    protected void notifyErrorListeners(AntlrError error) {
        errorListeners.forEach(listener -> listener.onError(error));
    }

    /**
     * 创建词法分析器
     */
    protected abstract Lexer createLexer(CharStream input);

    /**
     * 创建语法分析器
     */
    protected abstract Parser createParser(TokenStream tokens);

    /**
     * 执行解析并返回结果
     */
    protected abstract O parse(Parser parser) throws AntlrExecutionException;

    @Override
    public O execute(I input) throws AntlrExecutionException {
        try {
            CharStream charStream = createCharStream(input);
            Lexer lexer = createLexer(charStream);
            setupLexerErrorHandling(lexer);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Parser parser = createParser(tokens);
            setupParserErrorHandling(parser);
            return parse(parser);
        } catch (Exception e) {
            e.printStackTrace();
            //throw new AntlrExecutionException("ANTLR执行失败", e);
        }
        return null;
    }

    protected CharStream createCharStream(I input) throws IOException {
        if (input instanceof CharStream) {
            return (CharStream) input;
        }
        if (input instanceof String) {
            return CharStreams.fromString((String) input);
        }
        if (input instanceof InputStream) {
            return CharStreams.fromStream((InputStream) input);
        }
        throw new IllegalArgumentException("不支持的输入类型: " + input.getClass());
    }

    protected void setupLexerErrorHandling(Lexer lexer) {
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine,
                                    String msg, RecognitionException e) {
                AntlrError error = new AntlrError(msg, line, charPositionInLine, Collections.emptyList());
                notifyErrorListeners(error);
            }
        });
    }

    protected void setupParserErrorHandling(Parser parser) {
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine,
                                    String msg, RecognitionException e) {
                List<String> stack = ((Parser)recognizer).getRuleInvocationStack();
                Collections.reverse(stack);
                AntlrError error = new AntlrError(msg, line, charPositionInLine, stack);
                notifyErrorListeners(error);
            }
        });
    }
}
