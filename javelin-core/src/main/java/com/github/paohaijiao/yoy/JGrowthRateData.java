package com.github.paohaijiao.yoy;

import com.github.paohaijiao.yoy.enums.JTimeGranularity;

import java.time.temporal.Temporal;

public interface JGrowthRateData<T extends Temporal> {
    /**
     * 获取时间点（可以是LocalDate、YearMonth、Year等）
     */
    T getTimePoint();

    /**
     * 获取数值
     */
    Double getValue();

    /**
     * 获取增长率结果
     */
    Double getGrowthRate();

    /**
     * 设置增长率结果
     */
    void setGrowthRate(Double rate);

    /**
     * 获取时间粒度
     */
    JTimeGranularity getGranularity();
}
