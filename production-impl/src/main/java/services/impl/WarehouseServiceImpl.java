package services.impl;

import api.DeliveryNote;
import api.StorageUnit;
import api.WarehouseService;
import shortages.ShortagePredictionService;

public class WarehouseServiceImpl implements WarehouseService {

    //Inject all
    private ShortagePredictionService shortage;

    /**
     * <pre>
     * Register newly produced parts on stock.
     *  new parts are available on stock.
     *  If output from production is smaller than planned
     *  it may lead to shortage in next days.
     * </pre>
     */
    //Transactional
    @Override
    public void registerNew(StorageUnit unit) {
        shortage.processShortagesAfterStockChanged(unit.getProductRefNo());
    }

    /**
     * <pre>
     * Remove delivered parts from stock.
     *  If parts delivered during day exceed registered customer demand,
     *  demand for next day will be probably corrected with upcoming callof document,
     *  but in rare cases it may be caused by not registered additional delivery
     *  (lack of manual adjustments of demand in system).
     * </pre>
     */
    //Transactional
    @Override
    public void deliver(DeliveryNote note) {
        for (String productRefNo : note.getProducts()) {
            shortage.processShortagesAfterStockChanged(productRefNo);
        }
    }

}
