package shortages;

import entities.DemandEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExampleDemands {

    public static List<DemandEntity> demandSingleton(LocalDate date, long demand) {
        return Collections.singletonList(demand(demand).date(date).build());
    }

    public static List<DemandEntity> demandSequence(LocalDate startDate, long... demands) {
        List<DemandEntity> entities = new ArrayList<>(demands.length);
        LocalDate date = startDate;
        for (long demand : demands) {
            entities.add(demand(demand).date(date).build());
            date = date.plusDays(1);
        }
        return entities;
    }

    public static List<DemandEntity> demandSequence(LocalDate startDate, DemandBuilder... demands) {
        List<DemandEntity> entities = new ArrayList<>(demands.length);
        LocalDate date = startDate;
        for (DemandBuilder demand : demands) {
            entities.add(demand.date(date).build());
            date = date.plusDays(1);
        }
        return entities;
    }

    public static DemandBuilder demand(long level) {
        return DemandBuilder.demand(level);
    }
}
