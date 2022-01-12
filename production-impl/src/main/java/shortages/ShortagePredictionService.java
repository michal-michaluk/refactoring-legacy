package shortages;

import api.AdjustDemandDto;
import dao.DemandDao;
import dao.ProductionDao;
import dao.ShortageDao;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import external.JiraService;
import external.NotificationsService;
import external.StockService;
import tools.ShortageFinderACL;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionService {
    private final ShortageDao shortageDao;
    private final ProductionDao productionDao;
    private final StockService stockService;
    private final DemandDao demandDao;

    private final NotificationsService notificationService;
    private final JiraService jiraService;
    private final Clock clock;

    private final int confShortagePredictionDaysAhead;
    private final long confIncreaseQATaskPriorityInDays;

    public ShortagePredictionService(ShortageDao shortageDao, ProductionDao productionDao, StockService stockService, DemandDao demandDao, NotificationsService notificationService, JiraService jiraService, Clock clock, int confShortagePredictionDaysAhead, long confIncreaseQATaskPriorityInDays) {
        this.shortageDao = shortageDao;
        this.productionDao = productionDao;
        this.stockService = stockService;
        this.demandDao = demandDao;
        this.notificationService = notificationService;
        this.jiraService = jiraService;
        this.clock = clock;
        this.confShortagePredictionDaysAhead = confShortagePredictionDaysAhead;
        this.confIncreaseQATaskPriorityInDays = confIncreaseQATaskPriorityInDays;
    }

    public void processShortagesAfterStockChanged(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        CurrentStock currentStock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                today, confShortagePredictionDaysAhead,
                currentStock,
                productionDao.findFromTime(productRefNo, today.atStartOfDay()),
                demandDao.findFrom(today.atStartOfDay(), productRefNo)
        );

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (shortages != null && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            if (currentStock.getLocked() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    public void processShortagesAfterQualityChange(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        CurrentStock currentStock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                today, confShortagePredictionDaysAhead,
                currentStock,
                productionDao.findFromTime(productRefNo, today.atStartOfDay()),
                demandDao.findFrom(today.atStartOfDay(), productRefNo)
        );

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.softNotifyPlanner(shortages);
            if (currentStock.getLocked() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    public void processShortagesAfterDemandChanged(AdjustDemandDto adjustment) {
        String productRefNo = adjustment.getProductRefNo();
        LocalDate today = LocalDate.now(clock);
        CurrentStock stock = stockService.getCurrentStock(productRefNo);
        List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                today, confShortagePredictionDaysAhead,
                stock,
                productionDao.findFromTime(productRefNo, today.atStartOfDay()),
                demandDao.findFrom(today.atStartOfDay(), productRefNo)
        );
        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        // TODO REFACTOR: lookup for shortages -> ShortageFound / ShortagesGone
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            // TODO REFACTOR: policy why to increase task priority
            if (stock.getLocked() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
            shortageDao.save(shortages);
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    public void processShortagesAfterProductionPlanChangeed(List<ProductionEntity> products) {
        LocalDate today = LocalDate.now(clock);

        for (ProductionEntity production : products) {
            CurrentStock currentStock = stockService.getCurrentStock(production.getForm().getRefNo());
            List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                    today, confShortagePredictionDaysAhead,
                    currentStock,
                    productionDao.findFromTime(production.getForm().getRefNo(), today.atStartOfDay()),
                    demandDao.findFrom(today.atStartOfDay(), production.getForm().getRefNo())
            );
            List<ShortageEntity> previous = shortageDao.getForProduct(production.getForm().getRefNo());
            if (!shortages.isEmpty() && !shortages.equals(previous)) {
                notificationService.markOnPlan(shortages);
                if (currentStock.getLocked() > 0 &&
                        shortages.get(0).getAtDay()
                                .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                    jiraService.increasePriorityFor(production.getForm().getRefNo());
                }
                shortageDao.save(shortages);
            }
            if (shortages.isEmpty() && !previous.isEmpty()) {
                shortageDao.delete(production.getForm().getRefNo());
            }
        }
    }
}
