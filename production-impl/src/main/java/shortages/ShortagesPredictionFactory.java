package shortages;

import dao.DemandDao;
import dao.ProductionDao;
import external.CurrentStock;
import external.StockService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ShortagesPredictionFactory {

    private ProductionDao productionDao;
    private StockService stockService;
    private DemandDao demandDao;

    public ShortagePrediction create(String productRefNo, LocalDate today, int daysAhead) {
        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        CurrentStock stock = stockService.getCurrentStock(productRefNo);
        ProductionOutputs outputs = new ProductionOutputs(productionDao.findFromTime(productRefNo, today.atStartOfDay()));
        Demands demandsPerDay = new Demands(demandDao.findFrom(today.atStartOfDay(), productRefNo));

        return new ShortagePrediction(stock, dates, outputs, demandsPerDay);
    }
}
