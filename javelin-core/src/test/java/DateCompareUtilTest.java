import com.paohaijiao.javelin.date.JDateCompareUtil;
import org.junit.Test;

import java.util.Date;

import static com.paohaijiao.javelin.enums.JOperatorEnums.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DateCompareUtilTest
{
    @Test
    public void testCompareWithEnumOperator() {
        Date earlierDate = new Date(122, 0, 1);  // 2022-01-01
        Date laterDate = new Date(123, 0, 1);    // 2023-01-01
        Date sameDate = new Date(earlierDate.getTime());

        // 大于
        assertFalse(JDateCompareUtil.compare(earlierDate, laterDate, GREATER_THAN));
        assertTrue(JDateCompareUtil.compare(laterDate, earlierDate, GREATER_THAN));

        // 大于等于
        assertFalse(JDateCompareUtil.compare(earlierDate, laterDate, GREATER_THAN_OR_EQUAL));
        assertTrue(JDateCompareUtil.compare(laterDate, earlierDate, GREATER_THAN_OR_EQUAL));
        assertTrue(JDateCompareUtil.compare(sameDate, earlierDate, GREATER_THAN_OR_EQUAL));

        // 小于
        assertTrue(JDateCompareUtil.compare(earlierDate, laterDate, LESS_THAN));
        assertFalse(JDateCompareUtil.compare(laterDate, earlierDate, LESS_THAN));

        // 小于等于
        assertTrue(JDateCompareUtil.compare(earlierDate, laterDate, LESS_THAN_OR_EQUAL));
        assertFalse(JDateCompareUtil.compare(laterDate, earlierDate, LESS_THAN_OR_EQUAL));
        assertTrue(JDateCompareUtil.compare(sameDate, earlierDate, LESS_THAN_OR_EQUAL));

        // 等于
        assertTrue(JDateCompareUtil.compare(earlierDate, sameDate, EQUAL));
        assertFalse(JDateCompareUtil.compare(earlierDate, laterDate, EQUAL));

        // 不等于
        assertTrue(JDateCompareUtil.compare(earlierDate, laterDate, NOT_EQUAL));
        assertFalse(JDateCompareUtil.compare(earlierDate, sameDate, NOT_EQUAL));
    }

    @Test
    public void testCompareWithStringOperator() {
        Date earlierDate = new Date(122, 0, 1);  // 2022-01-01
        Date laterDate = new Date(123, 0, 1);    // 2023-01-01

        assertTrue(JDateCompareUtil.compare(laterDate, earlierDate, ">"));
        assertTrue(JDateCompareUtil.compare(laterDate, earlierDate, ">="));
        assertTrue(JDateCompareUtil.compare(earlierDate, laterDate, "<"));
        assertTrue(JDateCompareUtil.compare(earlierDate, laterDate, "<="));
        assertTrue(JDateCompareUtil.compare(earlierDate, earlierDate, "=="));
        assertTrue(JDateCompareUtil.compare(earlierDate, laterDate, "!="));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDate1() {
        JDateCompareUtil.compare(null, new Date(), GREATER_THAN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDate2() {
        JDateCompareUtil.compare(new Date(), null, GREATER_THAN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullOperator() {
//        JDateCompareUtil.compare(new Date(), new Date(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStringOperator() {
        JDateCompareUtil.compare(new Date(), new Date(), "=>");
    }
}
