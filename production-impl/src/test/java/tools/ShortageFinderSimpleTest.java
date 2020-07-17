package tools;

import acl.ShortageFinderACL;
import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import org.junit.Test;
import shortages.ExampleDemands;
import shortages.ShortagesAssert;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class ShortageFinderSimpleTest {

    private LocalDate date = LocalDate.now();

    @Test
    public void calculateWithoutProductionsOnPlan() {
        // given
        CurrentStock stock = new CurrentStock(1000, 0);
        List<ProductionEntity> noProductions = withoutProductionsOnPlan();
        List<DemandEntity> demands = ExampleDemands.demandSingleton(date.plusDays(2), 1000);

        // when
        List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                date.plusDays(1), 7,
                stock,
                noProductions,
                demands
        );

        // then
        ShortagesAssert.assertThat(shortages).noShortagesFound();
    }

    private List<ProductionEntity> withoutProductionsOnPlan() {
        return Collections.emptyList();
    }
}
