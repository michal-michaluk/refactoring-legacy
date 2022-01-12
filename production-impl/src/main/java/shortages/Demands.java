package shortages;

import entities.DemandEntity;
import tools.Util;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Demands {

    private final Map<LocalDate, DailyDemand> demandsPerDay;

    public Demands(List<DemandEntity> demands) {
        this.demandsPerDay = demands.stream()
                .collect(Collectors.toUnmodifiableMap(
                        DemandEntity::getDay,
                        entity -> new Demands.DailyDemand(
                                Util.getLevel(entity),
                                LevelOnDeliveryVariantDecision.pickCalculationVariant(Util.getDeliverySchema(entity))))
                );
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
