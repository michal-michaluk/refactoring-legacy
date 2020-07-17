package shortages;

import entities.DemandEntity;
import entities.ManualAdjustmentEntity;
import entities.OriginalDemandEntity;
import enums.DeliverySchema;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class DemandBuilder {

    private static AtomicLong ids = new AtomicLong(0);

    private DemandEntity subject;

    public static DemandBuilder demand(long level) {
        return new DemandBuilder(LocalDate.now(), level);
    }

    private DemandBuilder(LocalDate date, long level) {
        this.subject = demand(date, level);
    }

    public DemandBuilder tillEndOfDay() {
        subject.getOriginal().setDeliverySchema(DeliverySchema.tillEndOfDay);
        return this;
    }

    public DemandBuilder adjustedTo(long adjustment) {
        ManualAdjustmentEntity entity = new ManualAdjustmentEntity(adjustment, "", DeliverySchema.atDayStart);
        subject.getAdjustment().add(entity);
        return this;
    }

    public DemandBuilder date(LocalDate date) {
        subject.setCallofDate(date.minusDays(2));
        subject.setAtDay(date);
        subject.getOriginal().setAtDay(date);
        return this;
    }

    public DemandEntity build() {
        return subject;
    }

    private DemandEntity demand(LocalDate date, long level) {
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
