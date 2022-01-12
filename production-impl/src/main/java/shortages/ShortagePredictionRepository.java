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
import java.util.stream.Collectors;
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

        List<LocalDate> dates = Stream.iterate(today, date -> date.plusDays(1))
                .limit(daysAhead)
                .collect(toList());

        ProductionOutputs outputs = getProductionOutputs(productRefNo, today);
        Demands demandsPerDay = getDemands(productRefNo, today);
        return new ShortagePrediction(productRefNo, stock, dates, outputs, demandsPerDay);
    }

    private Demands getDemands(String productRefNo, LocalDate today) {
        List<DemandEntity> entities = demandDao.findFrom(today.atStartOfDay(), productRefNo);

        return new Demands(entities.stream()
                .collect(Collectors.toUnmodifiableMap(
                        DemandEntity::getDay,
                        entity -> new Demands.DailyDemand(
                                Util.getLevel(entity),
                                LevelOnDeliveryVariantDecision.pickCalculationVariant(Util.getDeliverySchema(entity))))
                ));
    }

    private ProductionOutputs getProductionOutputs(String productRefNo, LocalDate today) {
        List<ProductionEntity> productions = productionDao.findFromTime(productRefNo, today.atStartOfDay());
        return new ProductionOutputs(
                productions.stream()
                        .collect(Collectors.groupingBy(
                                production -> production.getStart().toLocalDate(),
                                Collectors.summingLong(ProductionEntity::getOutput)
                        ))
        );
    }
}
