package services.impl;

import api.AdjustDemandDto;
import api.LogisticService;
import api.StockForecastDto;
import dao.DemandDao;
import dao.ProductionDao;
import dao.ShortageDao;
import entities.DemandEntity;
import entities.ManualAdjustmentEntity;
import external.JiraService;
import external.NotificationsService;
import external.StockService;
import shortages.ShortagePredictionService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.LinkedList;

public class LogisticServiceImpl implements LogisticService {

    //Inject all
    private DemandDao demandDao;
    private ShortagePredictionService shortage;

    private ShortageDao shortageDao;
    private StockService stockService;
    private ProductionDao productionDao;

    private NotificationsService notificationService;
    private JiraService jiraService;
    private Clock clock;

    private int confShortagePredictionDaysAhead;
    private long confIncreaseQATaskPriorityInDays;

    /**
     * <pre>
     * Adjust demand at day to amount, delivered.
     *  New demand is stored for further reference
     *   We can change only Demands for today and future.
     *  Data from callof document should be preserved in database (DON’T OVERRIDE THEM).
     *   Should be possible to adjust demand even
     *  if there was no callof document for that product.
     *    Logistician note should be kept along with adjustment.
     *  If new demand is not fulfilled by  current product stock and production forecast
     *    there is a shortage in particular days and we need to rise an alert.
     *    planner should be notified,
     *    if there are locked parts on stock,
     *      QA task for recovering them should have high priority.
     * </pre>
     *
     * @param adjustment
     */
    //Transactional
    @Override
    public void adjustDemand(AdjustDemandDto adjustment) {
        if (adjustment.getAtDay().isBefore(LocalDate.now(clock))) {
            return; // TODO it is UI issue or reproduced post
        }
        DemandEntity demand = demandDao.getCurrent(adjustment.getProductRefNo(), adjustment.getAtDay());

        ManualAdjustmentEntity manualAdjustment = new ManualAdjustmentEntity();
        manualAdjustment.setLevel(adjustment.getLevel());
        manualAdjustment.setNote(adjustment.getNote());
        manualAdjustment.setDeliverySchema(adjustment.getDeliverySchema());

        if (demand.getAdjustment() == null) {
            demand.setAdjustment(new LinkedList<>());
        }
        demand.getAdjustment().add(manualAdjustment);
        // TODO REFACTOR: introduce domain event DemandManuallyAdjusted

        shortage.processShortagesAfterDemandChanged(adjustment);
    }

    /**
     * <pre>
     * Daily processing of callof document:
     * for all products included in callof document
     *   New demand are stored for further reference
     *   If new demand is not fulfilled by product stock and production forecast
     *     there is a shortage in particular days and we need to rise an alert.
     *     planner should be notified in that case,
     *     if there are locked parts on stock,
     *       QA task for recovering them should have high priority.
     * </pre>
     *
     * @param document
     */
    //Transactional
    @Override
    public void processCallof(Object document) {
        // TODO implement me later
        // processShortages()
    }

    //ReadOnly
    @Override
    public StockForecastDto getStockForecast(String productRefNo) {
        return new StockForecastDto();
    }

}
