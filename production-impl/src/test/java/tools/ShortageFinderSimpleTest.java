package tools;

import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import org.junit.Test;
import shortages.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ShortageFinderSimpleTest {

    private LocalDate date = LocalDate.now();

    @Test
    public void calculateWithoutProductionsOnPlan() {
        var stock = new CurrentStock(1000, 0);
        var noProductions = withoutProductionsOnPlan();
        var demands = ExampleDemands.demandSingleton(date.plusDays(2), 1000);

        List<ShortageEntity> shortages = whenShortagePredicted(stock, noProductions, demands);

        ShortagesAssert.assertThat(shortages)
                .noShortagesFound();
    }

    private List<ShortageEntity> whenShortagePredicted(CurrentStock stock, List<ProductionEntity> productions, List<DemandEntity> demands) {
        List<LocalDate> dates = Stream.iterate(date.plusDays(1), date -> date.plusDays(1))
                .limit(7)
                .collect(toList());

        ProductionOutputs outputs = new ProductionOutputs(productions);
        Demands demandsPerDay = new Demands(demands);
        return new ShortagePrediction(stock, dates, outputs, demandsPerDay).predict();
    }

    private List<ProductionEntity> withoutProductionsOnPlan() {
        return Collections.emptyList();
    }
}
