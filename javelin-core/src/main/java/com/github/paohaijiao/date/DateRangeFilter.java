package com.github.paohaijiao.date;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 日期区间元素提取工具（单方法版）
 */
public class DateRangeFilter {

    /**
     * 从List中提取指定日期区间内的元素
     *
     * @param list              源列表（不能为空）
     * @param dateExtractor     从列表元素中提取日期字段的函数（例如：User::getCreateTime）
     * @param startDateTime     开始时间（null表示不限制开始）
     * @param endDateTime       结束时间（null表示不限制结束）
     * @param includeStart      是否包含开始时间（true=包含，false=不包含）
     * @param includeEnd        是否包含结束时间（true=包含，false=不包含）
     * @param <T>               列表元素类型
     * @return 符合日期区间的元素列表
     * @throws IllegalArgumentException 源列表为空时抛出
     */
    public static <T> List<T> filterByDateTimeRange(List<T> list, Function<T, LocalDateTime> dateExtractor, LocalDateTime startDateTime, LocalDateTime endDateTime, boolean includeStart, boolean includeEnd) {
        if (list == null) {
            throw new IllegalArgumentException("源列表不能为空");
        }
        if (dateExtractor == null) {
            throw new IllegalArgumentException("日期提取函数不能为空");
        }
        if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        List<T> result = new ArrayList<>();
        for (T element : list) {
            if (element == null) {
                continue;
            }
            LocalDateTime elementTime = dateExtractor.apply(element); // 提取元素的日期字段（跳过日期为空的元素）
            if (elementTime == null) {
                continue;
            }
            boolean inRange = true;// 3. 日期区间判断
            if (startDateTime != null) {
                if (includeStart) {
                    inRange = !elementTime.isBefore(startDateTime); // >= 开始时间
                } else {
                    inRange = elementTime.isAfter(startDateTime);  // > 开始时间
                }
            }
            if (inRange && endDateTime != null) {// 判断结束时间边界（仅当开始时间判断通过时）
                if (includeEnd) {
                    inRange = !elementTime.isAfter(endDateTime);  // <= 结束时间
                } else {
                    inRange = elementTime.isBefore(endDateTime);   // < 结束时间
                }
            }
            if (inRange) {// 4. 符合条件则加入结果
                result.add(element);
            }
        }

        return result;
    }

    public static <T> List<T> filterByDateTimeRange(List<T> list, Function<T, LocalDateTime> dateExtractor, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return filterByDateTimeRange(list, dateExtractor, startDateTime, endDateTime, true, true);
    }
}
