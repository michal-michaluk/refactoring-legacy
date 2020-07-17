package shortages;

import entities.ShortageEntity;
import external.CurrentStock;

import java.time.LocalDate;
import java.util.List;

public class ShortagePrediction {
    private final String refNo;
    private final CurrentStock stock;
    private final List<LocalDate> dates;
    private final ProductionOutputs outputs;
    private final Demands demandsPerDay;

    public ShortagePrediction(String refNo, CurrentStock stock, List<LocalDate> dates, ProductionOutputs outputs, Demands demandsPerDay) {
        this.refNo = refNo;
        this.stock = stock;
        this.dates = dates;
        this.outputs = outputs;
        this.demandsPerDay = demandsPerDay;
    }

    public List<ShortageEntity> predict() {
        long level = stock.getLevel();

        ShortageBuilder gap = ShortageBuilder.builder(refNo);
        for (LocalDate day : dates) {
            if (!demandsPerDay.contains(day)) {
                level += outputs.get(day);
                continue;
            }
            Demands.DailyDemand demand = demandsPerDay.get(day);
            long produced = outputs.get(day);
            long levelOnDelivery = demand.calculate(level, produced);

            if (levelOnDelivery < 0) {
                gap.add(day, levelOnDelivery);
            }
            long endOfDayLevel = level + produced - demand.getLevel();
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return gap.build();
    }

    public CurrentStock getStock() {
        return stock;
    }
}
