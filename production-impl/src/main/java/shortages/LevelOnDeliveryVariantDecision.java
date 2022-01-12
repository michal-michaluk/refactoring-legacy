package shortages;

import enums.DeliverySchema;

import java.util.Map;

public class LevelOnDeliveryVariantDecision {

    private static final Map<DeliverySchema, LevelOnDeliveryCalculation> map = Map.of(
            DeliverySchema.atDayStart, LevelOnDeliveryCalculation.deliveryAtDayStart,
            DeliverySchema.tillEndOfDay, LevelOnDeliveryCalculation.deliveryTillEndOfDay,
            DeliverySchema.every3hours, LevelOnDeliveryCalculation.notImplemented
    );

    public static LevelOnDeliveryCalculation pickCalculationVariant(DeliverySchema schema) {
        return map.getOrDefault(schema, LevelOnDeliveryCalculation.notImplemented);
    }
}
