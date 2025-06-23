/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.antlr.impl;

import com.github.paohaijiao.antlr.JAntlrErrorListener;
import com.github.paohaijiao.antlr.JAntlrExecutor;
import com.github.paohaijiao.exception.JAntlrError;
import com.github.paohaijiao.exception.JAntlrExecutionException;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class JAbstractAntlrExecutor<I, O> implements JAntlrExecutor<I, O> {

    protected final List<JAntlrErrorListener> errorListeners = new ArrayList<>();

    @Override
    public void addErrorListener(JAntlrErrorListener listener) {
        errorListeners.add(listener);
    }

    @Override
    public void removeErrorListener(JAntlrErrorListener listener) {
        errorListeners.remove(listener);
    }

    protected void notifyErrorListeners(JAntlrError error) {
        errorListeners.forEach(listener -> listener.onError(error));
    }

    protected abstract Lexer createLexer(CharStream input);


    protected abstract Parser createParser(TokenStream tokens);


    protected abstract O parse(Parser parser) throws JAntlrExecutionException;

    @Override
    public O execute(I input) throws JAntlrExecutionException {
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
                JAntlrError error = new JAntlrError(msg, line, charPositionInLine, Collections.emptyList());
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
                JAntlrError error = new JAntlrError(msg, line, charPositionInLine, stack);
                notifyErrorListeners(error);
            }
        });
    }
}
