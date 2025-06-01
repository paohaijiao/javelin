package com.paohaijiao.javelin.i18n;

import java.util.Locale;

public interface MessageFormatter {
    String format(String pattern, Locale locale, Object... args);
}
