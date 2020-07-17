package shortages;

import dao.DemandDao;
import dao.ProductionDao;
import entities.DemandEntity;
import entities.ProductionEntity;
import external.CurrentStock;
import external.StockService;
import tools.Util;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
        Map<Object, Long> outputs = productionDao.findFromTime(productRefNo, today.atStartOfDay()).stream()
                .collect(Collectors.groupingBy(
                        production -> production.getStart().toLocalDate(),
                        Collectors.summingLong(ProductionEntity::getOutput)
                ));
        Map<LocalDate, Demands.DailyDemand> demands = demandDao.findFrom(today.atStartOfDay(), productRefNo).stream().collect(Collectors.toMap(
                DemandEntity::getDay,
                entity -> new Demands.DailyDemand(
                        Util.getLevel(entity),
                        LevelOnDelivery.pick(Util.getDeliverySchema(entity))
                )
        ));

        return new ShortagePrediction(
                productRefNo, stock, dates,
                new ProductionOutputs(outputs),
                new Demands(demands)
        );
    }
}
