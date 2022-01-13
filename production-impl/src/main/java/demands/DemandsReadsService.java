package demands;

import dao.DemandDao;
import entities.DemandEntity;
import enums.DeliverySchema;
import tools.Util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DemandsReadsService {
    private final DemandDao demandDao;

    public DemandsReadsService(DemandDao demandDao) {
        this.demandDao = demandDao;
    }

    public List<Demand> findFrom(LocalDateTime today, String productRefNo) {
        List<DemandEntity> entities = demandDao.findFrom(today, productRefNo);
        return entities.stream().map(entity -> new Demand(
                        today.toLocalDate(),
                        Util.getLevel(entity),
                        Util.getDeliverySchema(entity)))
                .collect(Collectors.toUnmodifiableList());
    }

    public static class Demand {
        private final LocalDate date;
        private final long demand;
        private final DeliverySchema schema;

        public Demand(LocalDate date, long demand, DeliverySchema schema) {
            this.date = date;
            this.demand = demand;
            this.schema = schema;
        }

        public LocalDate getDate() {
            return date;
        }

        public long getDemand() {
            return demand;
        }

        public DeliverySchema getSchema() {
            return schema;
        }
    }
}
