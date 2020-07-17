package tools;

import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import org.junit.Test;
import shortages.ExampleDemands;
import shortages.ShortagePrediction;
import shortages.ShortagesAssert;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

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
        ShortagePrediction subject = new SohrtagePredictionAssembler().demands(demands).outputs(productions).stock(stock).build(date);
        return subject.predict();
    }

    private List<ProductionEntity> withoutProductionsOnPlan() {
        return Collections.emptyList();
    }
}
