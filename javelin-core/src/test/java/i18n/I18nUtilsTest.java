package i18n;

import com.github.paohaijiao.i18n.I18nUtils;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

public class I18nUtilsTest {

    @Test
    void testDefaultLocale() {
        I18nUtils.setLocale(Locale.ENGLISH);
        assertEquals("Welcome to our system", I18nUtils.getMessage("welcome"));
    }

    @Test
    void testChineseLocale() {
        I18nUtils.setLocale(Locale.SIMPLIFIED_CHINESE);
        assertEquals("欢迎使用我们的系统", I18nUtils.getMessage("welcome"));
        assertEquals("欢迎使用我们的系统", I18nUtils.getMessage("i18n/messages", "welcome"));
    }

    @Test
    void testMessageWithParameters() {
        I18nUtils.clearCache();
        I18nUtils.setLocale(Locale.JAPANESE);
        String result = I18nUtils.getMessage("i18n/messages", "info.error", "网络错误");
        assertEquals("操作が失敗しました: 网络错误", result);
    }

    @Test
    void testMissingKey() {
        I18nUtils.setLocale(Locale.ENGLISH);
        assertEquals("???unknown.key???", I18nUtils.getMessage("unknown.key"));
    }

    @Test
    void testContainsKey() {
        assertTrue(I18nUtils.containsKey("button.submit"));
        assertFalse(I18nUtils.containsKey("button.delete"));
    }
}