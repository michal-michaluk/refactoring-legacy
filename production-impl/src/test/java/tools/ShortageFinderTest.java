package tools;

import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import org.junit.Test;
import shortages.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static shortages.ExampleDemands.demand;
import static shortages.ShortagesAssert.assertThat;

public class ShortageFinderTest {

    private LocalDate date = LocalDate.now();

    private CurrentStock stock;
    private List<ProductionEntity> productions;
    private List<DemandEntity> demands;

    private List<ShortageEntity> shortages;

    @Test
    public void findShortages() {
        given(
                new CurrentStock(1000, 200),
                ExampleProductionsPlan.productions(),
                ExampleDemands.demandSequence(date.plusDays(2), 17000, 17000)
        );

        whenShortagePredicted();

        thenShortages(shortages)
                .foundExactly(2)
                .missingPartsAt(date.plusDays(2), 3400)
                .missingPartsAt(date.plusDays(3), 7800);
    }

    @Test
    public void demandsWitAdjustement() {
        given(
                new CurrentStock(1000, 200),
                ExampleProductionsPlan.productions(),
                ExampleDemands.demandSequence(date.plusDays(2),
                        demand(17000).tillEndOfDay(),
                        demand(17000).adjustedTo(20000)
                )
        );

        whenShortagePredicted();

        thenShortages(shortages)
                .foundExactly(1)
                .missingPartsAt(date.plusDays(3), 10800);
    }

    private void given(CurrentStock currentStock, List<ProductionEntity> productions, List<DemandEntity> demandSequence) {
        this.stock = currentStock;
        this.productions = productions;
        this.demands = demandSequence;
    }

    private void whenShortagePredicted() {
        List<LocalDate> dates = Stream.iterate(date.plusDays(1), date -> date.plusDays(1))
                .limit(7)
                .collect(toList());

        ProductionOutputs outputs = new ProductionOutputs(productions);
        Demands demandsPerDay = new Demands(demands);
        shortages = new ShortagePrediction(stock, dates, outputs, demandsPerDay).predict();
    }

    private ShortagesAssert thenShortages(List<ShortageEntity> shortages) {
        return assertThat(shortages);
    }
}
