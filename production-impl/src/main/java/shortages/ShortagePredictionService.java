package shortages;

import acl.ShortageFinderACL;
import dao.DemandDao;
import dao.ProductionDao;
import dao.ShortageDao;
import entities.ShortageEntity;
import external.CurrentStock;
import external.JiraService;
import external.NotificationsService;
import external.StockService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionService {

    private ShortageDao shortageDao;
    private ProductionDao productionDao;
    private StockService stockService;
    private DemandDao demandDao;

    private NotificationsService notificationService;
    private JiraService jiraService;
    private Clock clock;

    private int confShortagePredictionDaysAhead;
    private long confIncreaseQATaskPriorityInDays;

    private ShortagerPredictionFactory factory;

    public ShortagePredictionService(ShortageDao shortageDao, ProductionDao productionDao, StockService stockService, DemandDao demandDao, NotificationsService notificationService, JiraService jiraService, Clock clock, int confShortagePredictionDaysAhead, long confIncreaseQATaskPriorityInDays, ShortagerPredictionFactory factory) {
        this.shortageDao = shortageDao;
        this.productionDao = productionDao;
        this.stockService = stockService;
        this.demandDao = demandDao;
        this.notificationService = notificationService;
        this.jiraService = jiraService;
        this.clock = clock;
        this.confShortagePredictionDaysAhead = confShortagePredictionDaysAhead;
        this.confIncreaseQATaskPriorityInDays = confIncreaseQATaskPriorityInDays;
        this.factory = factory;
    }

    public List<ShortageEntity> predictShortagres() {
        ShortagePrediction shortagePrediction = factory.create();
        return shortagePrediction.predict();
    }

    public void processShortagesFromWarehouse(String productRefNo) {
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
            notificationService.alertPlanner(shortages);
            if (currentStock.getLocked() > 0 &&
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

    public void processShortagesFromQuality(String productRefNo) {
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
            shortageDao.save(shortages);
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    public void processShortagesFromPlanning(List<String> productRefNos) {
        LocalDate today = LocalDate.now(clock);

        for (String productRefNo : productRefNos) {
            CurrentStock currentStock = stockService.getCurrentStock(productRefNo);
            List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                    today, confShortagePredictionDaysAhead,
                    currentStock,
                    productionDao.findFromTime(productRefNo, today.atStartOfDay()),
                    demandDao.findFrom(today.atStartOfDay(), productRefNo)
            );
            List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
            if (!shortages.isEmpty() && !shortages.equals(previous)) {
                notificationService.markOnPlan(shortages);
                if (currentStock.getLocked() > 0 &&
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
    }

    public void procesShortagesFromLogistic(String productRefNo) {
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


}
