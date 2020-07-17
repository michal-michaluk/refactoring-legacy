package shortages;

import java.time.LocalDate;
import java.util.Map;

public class Demands {

    private final Map<LocalDate, DailyDemand> demandsPerDay;

    public Demands(Map<LocalDate, DailyDemand> demands) {
        demandsPerDay = demands;
    }

    public DailyDemand get(LocalDate day) {
        return demandsPerDay.get(day);
    }

    public boolean contains(LocalDate day) {
        return demandsPerDay.containsKey(day);
    }

    public static class DailyDemand {

        private final long level;
        private final LevelOnDelivery str;

        public DailyDemand(long level, LevelOnDelivery str) {
            this.level = level;
            this.str = str;
        }

        public long getLevel() {
            return level;
        }

        public long calculate(long stock, long produced) {
            return str.calculate(stock, this.level, produced);
        }
    }
}
