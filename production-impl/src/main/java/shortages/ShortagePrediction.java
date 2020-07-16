package shortages;

import entities.ShortageEntity;
import external.CurrentStock;

import java.time.LocalDate;
import java.util.List;

public class ShortagePrediction {
    private CurrentStock stock;
    private List<LocalDate> dates;
    private ProductionOutputs outputs;
    private Demands demandsPerDay;

    public ShortagePrediction(CurrentStock stock, List<LocalDate> dates, ProductionOutputs outputs, Demands demandsPerDay) {
        this.stock = stock;
        this.dates = dates;
        this.outputs = outputs;
        this.demandsPerDay = demandsPerDay;
    }

    public List<ShortageEntity> predict() {
        long level = stock.getLevel();

        ShortageBuilder gap = ShortageBuilder.builder(outputs.getProductRefNo());
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
}
