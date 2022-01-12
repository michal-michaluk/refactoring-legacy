package shortages;

import entities.DemandEntity;
import entities.ProductionEntity;
import external.CurrentStock;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ShortagePredictionFactory {
    private final CurrentStock stock;
    private final List<ProductionEntity> productions;
    private final List<DemandEntity> demands;

    public ShortagePredictionFactory(CurrentStock stock, List<ProductionEntity> productions, List<DemandEntity> demands) {
        this.stock = stock;
        this.productions = productions;
        this.demands = demands;
    }

    public ShortagePrediction create(LocalDate today, int daysAhead) {
        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        ProductionOutputs outputs = new ProductionOutputs(productions);
        Demands demandsPerDay = new Demands(demands);
        return new ShortagePrediction(stock, dates, outputs, demandsPerDay);
    }
}
