package fin;

import com.github.paohaijiao.yoy.JYoYCalculator;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class JMultiGranularityTest {
    @Test
    public void yoy() throws IOException {
        List<MonthlyData> mixedData = new ArrayList<>();
        mixedData.add(new MonthlyData(YearMonth.of(2024, 1), 100.0));
        mixedData.add(new MonthlyData(YearMonth.of(2024, 2), 200.0));
        mixedData.add(new MonthlyData(YearMonth.of(2024, 3), 300.0));
        mixedData.add(new MonthlyData(YearMonth.of(2024, 4), 400.0));
        Function<List<MonthlyData>, List<MonthlyData>> chainRatio = JYoYCalculator.calculateChainRatio();
        List<MonthlyData> chainResults = chainRatio.apply(mixedData);
        chainResults.forEach(System.out::println);

    }
}
