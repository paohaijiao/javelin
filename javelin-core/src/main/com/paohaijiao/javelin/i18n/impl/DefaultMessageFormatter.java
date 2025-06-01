package com.paohaijiao.javelin.i18n.impl;

import com.paohaijiao.javelin.i18n.MessageFormatter;

import java.text.MessageFormat;
import java.util.Locale;

public class DefaultMessageFormatter implements MessageFormatter {
    @Override
    public String format(String pattern, Locale locale, Object... args) {
        if (args == null || args.length == 0) {
            return pattern;
        }
        MessageFormat format = new MessageFormat(pattern, locale);
        return format.format(args);
    }
}
