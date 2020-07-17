package shortages;

import dao.ShortageDao;
import entities.ShortageEntity;
import external.JiraService;
import external.NotificationsService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ShortagePredictionService {

    private ShortageDao shortageDao;

    private NotificationsService notificationService;
    private JiraService jiraService;
    private Clock clock;

    private int confShortagePredictionDaysAhead;
    private long confIncreaseQATaskPriorityInDays;

    private ShortagesPredictionRepository factory;

    public void processShortagesFromWarehouse(String productRefNo) {
        LocalDate today = LocalDate.now(clock);

        ShortagePrediction shortagePrediction = factory.create(productRefNo, today, confShortagePredictionDaysAhead);
        List<ShortageEntity> shortages = shortagePrediction.predict();

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            if (shortagePrediction.getStock().getLocked() > 0 &&
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

        ShortagePrediction shortagePrediction = factory.create(productRefNo, today, confShortagePredictionDaysAhead);
        List<ShortageEntity> shortages = shortagePrediction.predict();

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.softNotifyPlanner(shortages);
            if (shortagePrediction.getStock().getLocked() > 0 &&
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
            ShortagePrediction shortagePrediction = factory.create(productRefNo, today, confShortagePredictionDaysAhead);
            List<ShortageEntity> shortages = shortagePrediction.predict();

            List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
            if (!shortages.isEmpty() && !shortages.equals(previous)) {
                notificationService.markOnPlan(shortages);
                if (shortagePrediction.getStock().getLocked() > 0 &&
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

        ShortagePrediction shortagePrediction = factory.create(productRefNo, today, confShortagePredictionDaysAhead);
        List<ShortageEntity> shortages = shortagePrediction.predict();

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        // TODO REFACTOR: lookup for shortages -> ShortageFound / ShortagesGone
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            // TODO REFACTOR: policy why to increase task priority
            if (shortagePrediction.getStock().getLocked() > 0 &&
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
