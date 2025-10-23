package fin;


import com.github.paohaijiao.yoy.JGrowthRateData;
import com.github.paohaijiao.yoy.enums.JTimeGranularity;

import java.time.YearMonth;

public class QuarterlyData implements JGrowthRateData<YearMonth> {

    private final YearMonth quarterStart;

    private final Double value;

    private Double growthRate;

    public QuarterlyData(int year, int quarter, Double value) {
        int startMonth = (quarter - 1) * 3 + 1;
        this.quarterStart = YearMonth.of(year, startMonth);
        this.value = value;
    }

    @Override
    public YearMonth getTimePoint() {
        return quarterStart;
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
        return JTimeGranularity.QUARTERLY;
    }

    @Override
    public String toString() {
        int quarter = (quarterStart.getMonthValue() - 1) / 3 + 1;
        return String.format("QuarterlyData{%d-Q%d, value=%.2f, growthRate=%s}", quarterStart.getYear(), quarter, value, growthRate == null ? "N/A" : String.format("%.2f%%", growthRate));
    }
}
