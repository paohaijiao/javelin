package fin;


import com.github.paohaijiao.yoy.JGrowthRateData;
import com.github.paohaijiao.yoy.enums.JTimeGranularity;

import java.time.YearMonth;

public class MonthlyData implements JGrowthRateData<YearMonth> {

    private final YearMonth month;

    private final Double value;

    private Double growthRate;

    public MonthlyData(YearMonth month, Double value) {
        this.month = month;
        this.value = value;
    }

    public MonthlyData(int year, int month, Double value) {
        this(YearMonth.of(year, month), value);
    }

    @Override
    public YearMonth getTimePoint() {
        return month;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public Double getGrowthRate() {
        return growthRate;
    }

    @Override
    public void setGrowthRate(Double rate) {
        this.growthRate = rate;
    }

    @Override
    public JTimeGranularity getGranularity() {
        return JTimeGranularity.MONTHLY;
    }

    @Override
    public String toString() {
        return String.format("MonthlyData{month=%s, value=%.2f, growthRate=%s}", month, value, growthRate == null ? "N/A" : String.format("%.2f%%", growthRate));
    }
}
