package fin;


import com.github.paohaijiao.yoy.JGrowthRateData;
import com.github.paohaijiao.yoy.enums.JTimeGranularity;

import java.time.Year;

public class YearlyData implements JGrowthRateData<Year> {

    private final Year year;

    private final Double value;

    private Double growthRate;

    public YearlyData(Year year, Double value) {
        this.year = year;
        this.value = value;
    }

    public YearlyData(int year, Double value) {
        this(Year.of(year), value);
    }

    @Override
    public Year getTimePoint() {
        return year;
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
        return JTimeGranularity.YEARLY;
    }

    @Override
    public String toString() {
        return String.format("YearlyData{year=%s, value=%.2f, growthRate=%s}", year, value, growthRate == null ? "N/A" : String.format("%.2f%%", growthRate));
    }
}
