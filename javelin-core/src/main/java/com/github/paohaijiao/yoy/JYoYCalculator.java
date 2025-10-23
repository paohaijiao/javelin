package com.github.paohaijiao.yoy;

import com.github.paohaijiao.yoy.enums.JTimeGranularity;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JYoYCalculator {
    /**
     * 计算环比（与上一个周期比较）
     */
    public static <T extends Temporal, D extends JGrowthRateData<T>> Function<List<D>, List<D>> calculateChainRatio() {
        return dataList -> {
            if (dataList.isEmpty()) return dataList;
            Map<JTimeGranularity, List<D>> groupedData = dataList.stream().collect(Collectors.groupingBy(JGrowthRateData::getGranularity));
            return groupedData.values().stream().flatMap(list -> calculateForGranularity(list).stream()).collect(Collectors.toList());
        };
    }

    /**
     * 计算同比（与去年同期比较）
     */
    public static <T extends Temporal, D extends JGrowthRateData<T>> Function<List<D>, List<D>> calculateYearOverYearRatio() {
        return dataList -> {
            Map<String, D> timeKeyMap = dataList.stream().collect(Collectors.toMap(data -> JTimeGranularityUtils.getTimeKey(data.getTimePoint(), data.getGranularity()), Function.identity()));
            return dataList.stream().map(current -> {
                        T previousYearTime = getPreviousYearTime(current.getTimePoint(), current.getGranularity());
                        String previousYearKey = JTimeGranularityUtils.getTimeKey(previousYearTime, current.getGranularity());
                        D previousYearData = timeKeyMap.get(previousYearKey);
                        Double growthRate = calculateGrowthRate(current.getValue(), Optional.ofNullable(previousYearData).map(JGrowthRateData::getValue).orElse(null));
                        current.setGrowthRate(growthRate);
                        return current;
                    })
                    .collect(Collectors.toList());
        };
    }

    /**
     * 为特定粒度计算环比
     */
    private static <T extends Temporal, D extends JGrowthRateData<T>> List<D> calculateForGranularity(List<D> dataList) {
        if (dataList.isEmpty()) return dataList;
        JTimeGranularity granularity = dataList.get(0).getGranularity();
        Map<String, D> timeKeyMap = dataList.stream().collect(Collectors.toMap(data -> JTimeGranularityUtils.getTimeKey(data.getTimePoint(), granularity), Function.identity()));
        return dataList.stream()
                .map(current -> {
                    T previousTime = JTimeGranularityUtils.getPreviousTimePoint(current.getTimePoint(), granularity);
                    String previousKey = JTimeGranularityUtils.getTimeKey(previousTime, granularity);
                    D previousData = timeKeyMap.get(previousKey);
                    Double growthRate = calculateGrowthRate(current.getValue(), Optional.ofNullable(previousData).map(JGrowthRateData::getValue).orElse(null));
                    current.setGrowthRate(growthRate);
                    return current;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取去年同期时间点
     */
    @SuppressWarnings("unchecked")
    private static <T extends Temporal> T getPreviousYearTime(T current, JTimeGranularity granularity) {
        switch (granularity) {
            case DAILY:
                return (T) ((LocalDate) current).minusYears(1);
            case WEEKLY:
                return (T) ((LocalDate) current).minusYears(1);
            case MONTHLY:
                if (current instanceof YearMonth) {
                    return (T) ((YearMonth) current).minusYears(1);
                } else {
                    return (T) ((LocalDate) current).minusYears(1);
                }
            case QUARTERLY:
                // 季度数据，年份减1
                if (current instanceof LocalDate) {
                    LocalDate date = (LocalDate) current;
                    return (T) date.minusYears(1);
                } else if (current instanceof YearMonth) {
                    YearMonth ym = (YearMonth) current;
                    return (T) ym.minusYears(1);
                } else {
                    throw new IllegalArgumentException("不支持的季度类型: " + current.getClass());
                }
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
     * 通用增长率计算
     */
    private static Double calculateGrowthRate(Double current, Double previous) {
        if (previous == null || previous == 0.0) {
            return null;
        }
        if (current == null) {
            return null;
        }
        return (current - previous) / previous * 100;
    }

    /**
     * 格式化结果
     */
    public static <T extends Temporal, D extends JGrowthRateData<T>> Function<List<D>, String> formatResults() {
        return results -> results.stream()
                .map(data -> {
                    String rate = data.getGrowthRate() == null ? "N/A" : String.format("%.2f%%", data.getGrowthRate());
                    return String.format("%s[%s]: %.2f → %s", data.getTimePoint(), data.getGranularity(), data.getValue(), rate);
                }).collect(Collectors.joining("\n"));
    }
}
