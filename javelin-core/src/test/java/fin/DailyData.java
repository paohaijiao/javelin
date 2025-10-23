package fin;


import com.github.paohaijiao.yoy.JGrowthRateData;
import com.github.paohaijiao.yoy.enums.JTimeGranularity;

import java.time.LocalDate;

public class DailyData implements JGrowthRateData<LocalDate> {

    private final LocalDate date;

    private final Double value;

    private Double growthRate;

    public DailyData(LocalDate date, Double value) {
        this.date = date;
        this.value = value;
    }

    @Override
    public LocalDate getTimePoint() {
        return date;
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
        return JTimeGranularity.DAILY;
    }

    @Override
    public String toString() {
        return String.format("DailyData{date=%s, value=%.2f, growthRate=%s}", date, value, growthRate == null ? "N/A" : String.format("%.2f%%", growthRate));
    }
}
