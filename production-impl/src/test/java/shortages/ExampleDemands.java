package shortages;

import entities.DemandEntity;
import entities.OriginalDemandEntity;
import enums.DeliverySchema;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ExampleDemands {

    private static AtomicLong ids = new AtomicLong(0);

    public static List<DemandEntity> demandSequence(LocalDate startDate, long... demands) {
        List<DemandEntity> entities = new ArrayList<>(demands.length);
        LocalDate date = startDate;
        for (long demand : demands) {
            entities.add(demand(date, demand));
            date = date.plusDays(1);
        }
        return entities;
    }

    public static List<DemandEntity> demandSingleton(LocalDate date, long demand) {
        return Collections.singletonList(demand(date, demand));
    }

    private static DemandEntity demand(LocalDate date, long level) {
        DemandEntity entity = new DemandEntity();
        entity.setId(ids.getAndIncrement());
        entity.setCallofDate(date.minusDays(2));
        entity.setProductRefNo("300900");
        entity.setAtDay(date);
        OriginalDemandEntity original = new OriginalDemandEntity();
        original.setAtDay(date);
        original.setLevel(level);
        original.setDeliverySchema(DeliverySchema.atDayStart);
        entity.setOriginal(original);
        entity.setAdjustment(new ArrayList<>());
        return entity;
    }
}
