//package org.paohaijiao.jstark.antlr.impl;
//
//import org.antlr.runtime.CharStream;
//import org.antlr.runtime.Lexer;
//
//public class CalculatorExecutor extends AbstractAntlrExecutor<String, Double> {
//    @Override
//    protected Lexer createLexer(CharStream input) {
//        return new CalculatorLexer(input);
//    }
//
//    @Override
//    protected Parser createParser(TokenStream tokens) {
//        return new CalculatorParser(tokens);
//    }
//
//    @Override
//    protected Double parse(Parser parser) throws AntlrExecutionException {
//        CalculatorParser calcParser = (CalculatorParser) parser;
//        CalculatorParser.ExprContext tree = calcParser.expr();
//
//        // 这里可以添加自定义的监听器或访问器
//        CalculatorVisitorImpl visitor = new CalculatorVisitorImpl();
//        return visitor.visit(tree);
//    }
//}
