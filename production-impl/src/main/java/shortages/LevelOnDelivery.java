package shortages;

import enums.DeliverySchema;

import java.util.Map;

public interface LevelOnDelivery {

    LevelOnDelivery Unsupported = (level, demand, produced) -> {throw new UnsupportedOperationException();};
    LevelOnDelivery AtDayStart = (level, demand, produced) -> level - demand;
    LevelOnDelivery TillEndOfDay = (level, demand, produced) -> level - demand + produced;

    Map<DeliverySchema, LevelOnDelivery> mapping = Map.of(
            DeliverySchema.atDayStart, AtDayStart,
            DeliverySchema.tillEndOfDay, TillEndOfDay
    );

    static LevelOnDelivery pick(DeliverySchema deliverySchema) {
        return mapping.getOrDefault(deliverySchema, Unsupported);
    }

    long calculate(long level, long demand, long produced);

}
