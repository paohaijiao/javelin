package org.paohaijiao.jstark.i18n;

import java.util.Locale;

public interface MessageFormatter {
    String format(String pattern, Locale locale, Object... args);
}
