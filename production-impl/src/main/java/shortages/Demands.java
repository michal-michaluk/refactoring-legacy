package shortages;

import java.time.LocalDate;
import java.util.Map;

public class Demands {

    private final Map<LocalDate, DailyDemand> demandsPerDay;

    public Demands(Map<LocalDate, DailyDemand> demandsPerDay) {
        this.demandsPerDay = demandsPerDay;
    }

    public DailyDemand get(LocalDate day) {
        return demandsPerDay.get(day);
    }

    public static class DailyDemand {
        private final long demand;
        private final LevelOnDeliveryCalculation strategy;

        public DailyDemand(long level, LevelOnDeliveryCalculation strategy) {
            demand = level;
            this.strategy = strategy;
        }

        public long getLevel() {
            return demand;
        }

        public long calculateLevelOnDelivery(long level, long produced) {
            return strategy.calculate(level, getLevel(), produced);
        }
    }
}
