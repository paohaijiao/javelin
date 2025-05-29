//package org.paohaijiao.jstark.antlr.impl;
//public class Main {
//    public static void main(String[] args) {
//        CalculatorExecutor executor = new CalculatorExecutor();
//
//        executor.addErrorListener(error -> {
//            System.err.printf("错误: 行%d:%d - %s%n",
//                    error.getLine(), error.getCharPosition(), error.getMessage());
//            System.err.println("规则栈: " + error.getRuleStack());
//        });
//
//        try {
//            Double result = executor.execute("3 + 5 * (10 - 4)");
//            System.out.println("结果: " + result);
//        } catch (AntlrExecutionException e) {
//            System.err.println("解析失败: " + e.getMessage());
//            e.getErrors().forEach(err ->
//                    System.err.println(" - " + err.getMessage()));
//        }
//    }
//}
