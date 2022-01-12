package shortages;

import external.CurrentStock;

import java.time.LocalDate;
import java.util.List;

public class ShortagePrediction {
    private final CurrentStock stock;
    private final List<LocalDate> dates;
    private final ProductionOutputs outputs;
    private final Demands demandsPerDay;

    public ShortagePrediction(CurrentStock stock, List<LocalDate> dates, ProductionOutputs outputs, Demands demandsPerDay) {
        this.stock = stock;
        this.dates = dates;
        this.outputs = outputs;
        this.demandsPerDay = demandsPerDay;
    }

    public ShortageBuilder predict() {
        long level = stock.getLevel();
        ShortageBuilder shortages = ShortageBuilder.builder(outputs.getProductRefNo());
        for (LocalDate day : dates) {
            Demands.DailyDemand demand = demandsPerDay.get(day);
            if (demand == null) {
                level += outputs.getLevel(day);
                continue;
            }
            long produced = outputs.getLevel(day);
            long levelOnDelivery = demand.calculateLevelOnDelivery(level, produced);

            if (levelOnDelivery < 0) {
                shortages.foundForDay(day, levelOnDelivery);
            }
            long endOfDayLevel = level + produced - demand.getLevel();
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return shortages;
    }

    public long getLocked() {
        return stock.getLocked();
    }
}
