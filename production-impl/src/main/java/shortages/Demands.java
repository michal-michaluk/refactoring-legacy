package shortages;

import entities.DemandEntity;
import enums.DeliverySchema;
import tools.Util;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Demands {

    private final Map<LocalDate, DemandEntity> demandsPerDay;

    public Demands(List<DemandEntity> demands) {
        this.demandsPerDay = demands.stream()
                .collect(Collectors.toUnmodifiableMap(
                        DemandEntity::getDay,
                        Function.identity()
                ));
    }

    public DailyDemand get(LocalDate day) {
        return Optional.ofNullable(demandsPerDay.get(day))
                .map(DailyDemand::new)
                .orElse(null);
    }

    public static class DailyDemand {
        private final DemandEntity entity;

        public DailyDemand(DemandEntity entity) {
            this.entity = entity;
        }

        public DeliverySchema getDeliverySchema() {
            return Util.getDeliverySchema(entity);
        }

        public long getLevel() {
            return Util.getLevel(entity);
        }
    }
}
