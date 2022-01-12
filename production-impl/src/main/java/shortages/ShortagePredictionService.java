package shortages;

import api.AdjustDemandDto;
import dao.ShortageDao;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.JiraService;
import external.NotificationsService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionService {
    private final ShortagePredictionRepository repository;
    private final ShortageDao shortageDao;

    private final NotificationsService notificationService;
    private final JiraService jiraService;
    private final Clock clock;

    private final int confShortagePredictionDaysAhead;
    private final long confIncreaseQATaskPriorityInDays;

    public ShortagePredictionService(ShortageDao shortageDao, ShortagePredictionRepository factory, NotificationsService notificationService, JiraService jiraService, Clock clock, int confShortagePredictionDaysAhead, long confIncreaseQATaskPriorityInDays) {
        this.shortageDao = shortageDao;
        this.repository = factory;
        this.notificationService = notificationService;
        this.jiraService = jiraService;
        this.clock = clock;
        this.confShortagePredictionDaysAhead = confShortagePredictionDaysAhead;
        this.confIncreaseQATaskPriorityInDays = confIncreaseQATaskPriorityInDays;
    }

    public void processShortagesAfterStockChanged(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        ShortagePrediction prediction = repository.get(productRefNo, today, confShortagePredictionDaysAhead);
        List<ShortageEntity> shortages = prediction.predict().build();

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (shortages != null && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            if (prediction.getLocked() > 0 &&
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
        ShortagePrediction prediction = repository.get(productRefNo, today, confShortagePredictionDaysAhead);
        List<ShortageEntity> shortages = prediction.predict().build();


        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.softNotifyPlanner(shortages);
            if (prediction.getLocked() > 0 &&
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
        ShortagePrediction prediction = repository.get(productRefNo, today, confShortagePredictionDaysAhead);
        List<ShortageEntity> shortages = prediction.predict().build();

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        // TODO REFACTOR: lookup for shortages -> ShortageFound / ShortagesGone
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            // TODO REFACTOR: policy why to increase task priority
            if (prediction.getLocked() > 0 &&
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
            String productRefNo = production.getForm().getRefNo();
            ShortagePrediction prediction = repository.get(productRefNo, today, confShortagePredictionDaysAhead);
            List<ShortageEntity> shortages = prediction.predict().build();
            List<ShortageEntity> previous = shortageDao.getForProduct(production.getForm().getRefNo());
            if (!shortages.isEmpty() && !shortages.equals(previous)) {
                notificationService.markOnPlan(shortages);
                if (prediction.getLocked() > 0 &&
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
