package shortages;

import dao.DemandDao;
import dao.ProductionDao;
import entities.DemandEntity;
import entities.ProductionEntity;
import external.CurrentStock;
import external.StockService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ShortagePredictionRepository {

    private final ProductionDao productionDao;
    private final StockService stockService;
    private final DemandDao demandDao;

    public ShortagePredictionRepository(ProductionDao productionDao, StockService stockService, DemandDao demandDao) {
        this.productionDao = productionDao;
        this.stockService = stockService;
        this.demandDao = demandDao;
    }

    public ShortagePrediction get(String productRefNo, LocalDate today, int daysAhead) {
        CurrentStock stock = stockService.getCurrentStock(productRefNo);
        List<ProductionEntity> productions = productionDao.findFromTime(productRefNo, today.atStartOfDay());
        List<DemandEntity> demands = demandDao.findFrom(today.atStartOfDay(), productRefNo);

        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        ProductionOutputs outputs = new ProductionOutputs(productions);
        Demands demandsPerDay = new Demands(demands);
        return new ShortagePrediction(stock, dates, outputs, demandsPerDay);
    }
}
