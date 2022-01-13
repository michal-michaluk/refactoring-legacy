package shortages;

import external.StockService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ShortagePredictionRepository {

    private final StockService stockService;
    private final DemandsRepository demands;
    private final ProductionOutputsRepository outputs;

    public ShortagePredictionRepository(StockService stockService, DemandsRepository demands, ProductionOutputsRepository outputs) {
        this.stockService = stockService;
        this.demands = demands;
        this.outputs = outputs;
    }

    public ShortagePrediction get(String productRefNo, LocalDate today, int daysAhead) {
        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        return new ShortagePrediction(
                productRefNo,
                dates,
                stockService.getCurrentStock(productRefNo),
                outputs.get(productRefNo, today),
                demands.get(productRefNo, today)
        );
    }
}
