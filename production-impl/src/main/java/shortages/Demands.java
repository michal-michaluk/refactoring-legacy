package shortages;

import entities.DemandEntity;
import enums.DeliverySchema;
import tools.Util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demands {

    private final Map<LocalDate, DemandEntity> demandsPerDay;

    public Demands(List<DemandEntity> demands) {
        demandsPerDay = new HashMap<>();
        for (DemandEntity demand1 : demands) {
            demandsPerDay.put(demand1.getDay(), demand1);
        }
    }

    public DailyDemand get(LocalDate day) {
        if (demandsPerDay.containsKey(day)) {
            DemandEntity entity = demandsPerDay.get(day);
            return new DailyDemand(
                    Util.getLevel(entity),
                    LevelOnDelivery.pick(Util.getDeliverySchema(entity))
            );
        }
        return null;
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
