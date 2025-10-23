package com.github.paohaijiao.yoy;

import com.github.paohaijiao.yoy.enums.JTimeGranularity;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.Temporal;

public class JTimeGranularityUtils {

    @SuppressWarnings("unchecked")
    public static <T extends Temporal> T getPreviousTimePoint(T current, JTimeGranularity granularity) {
        switch (granularity) {
            case DAILY:
                return (T) ((LocalDate) current).minusDays(1);
            case WEEKLY:
                return (T) ((LocalDate) current).minusWeeks(1);
            case MONTHLY:
                if (current instanceof YearMonth) {
                    return (T) ((YearMonth) current).minusMonths(1);
                } else {
                    return (T) ((LocalDate) current).minusMonths(1);
                }
            case QUARTERLY:
                return (T) getPreviousQuarter(current);
            case YEARLY:
                if (current instanceof Year) {
                    return (T) ((Year) current).minusYears(1);
                } else {
                    return (T) ((LocalDate) current).minusYears(1);
                }
            default:
                throw new IllegalArgumentException("不支持的粒度类型: " + granularity);
        }
    }

    /**
     * 获取前一个季度
     */
    private static Temporal getPreviousQuarter(Temporal current) {
        if (current instanceof LocalDate) {
            LocalDate date = (LocalDate) current;
            int currentQuarter = (date.getMonthValue() - 1) / 3;
            int currentYear = date.getYear();
            int prevQuarter = (currentQuarter - 1 + 4) % 4;
            int prevYear = currentQuarter == 0 ? currentYear - 1 : currentYear;
            int month = prevQuarter * 3 + 1;
            return YearMonth.of(prevYear, month);
        } else if (current instanceof YearMonth) {
            YearMonth ym = (YearMonth) current;
            int currentQuarter = (ym.getMonthValue() - 1) / 3;
            int currentYear = ym.getYear();
            int prevQuarter = (currentQuarter - 1 + 4) % 4;
            int prevYear = currentQuarter == 0 ? currentYear - 1 : currentYear;
            int month = prevQuarter * 3 + 1;
            return YearMonth.of(prevYear, month);
        } else {
            throw new IllegalArgumentException("不支持的季度计算类型: " + current.getClass());
        }
    }

    /**
     * 获取时间点的标准表示（用于Map的key）
     */
    public static String getTimeKey(Temporal timePoint, JTimeGranularity granularity) {
        switch (granularity) {
            case DAILY:
                return ((LocalDate) timePoint).toString();
            case WEEKLY:
                LocalDate date = (LocalDate) timePoint;
                return date.getYear() + "-W" + String.format("%02d", (date.getDayOfYear() - 1) / 7 + 1);
            case MONTHLY:
                if (timePoint instanceof YearMonth) {
                    return ((YearMonth) timePoint).toString();
                } else {
                    return YearMonth.from(timePoint).toString();
                }
            case QUARTERLY:
                return getQuarterKey(timePoint);
            case YEARLY:
                if (timePoint instanceof Year) {
                    return ((Year) timePoint).toString();
                } else {
                    return Year.from(timePoint).toString();
                }
            default:
                return timePoint.toString();
        }
    }

    /**
     * 获取季度key
     */
    private static String getQuarterKey(Temporal timePoint) {
        int year, quarter;
        if (timePoint instanceof LocalDate) {
            LocalDate date = (LocalDate) timePoint;
            year = date.getYear();
            quarter = (date.getMonthValue() - 1) / 3 + 1;
        } else if (timePoint instanceof YearMonth) {
            YearMonth ym = (YearMonth) timePoint;
            year = ym.getYear();
            quarter = (ym.getMonthValue() - 1) / 3 + 1;
        } else {
            throw new IllegalArgumentException("不支持的季度key类型: " + timePoint.getClass());
        }
        return year + "-Q" + quarter;
    }

    /**
     * 检测时间粒度
     */
    public static JTimeGranularity detectGranularity(Temporal timePoint) {
        if (timePoint instanceof Year) {
            return JTimeGranularity.YEARLY;
        } else if (timePoint instanceof YearMonth) {
            return JTimeGranularity.MONTHLY;
        } else if (timePoint instanceof LocalDate) {
            return JTimeGranularity.DAILY;
        } else {
            throw new IllegalArgumentException("不支持的时间类型: " + timePoint.getClass());
        }
    }
}
