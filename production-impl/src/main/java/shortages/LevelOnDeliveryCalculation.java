package shortages;

public interface LevelOnDeliveryCalculation {
    LevelOnDeliveryCalculation deliveryAtDayStart = (long level, long demand, long produced) -> level - demand;
    LevelOnDeliveryCalculation deliveryTillEndOfDay = (long level, long demand, long produced) -> level - demand + produced;
    LevelOnDeliveryCalculation notImplemented = (long level, long demand, long produced) -> {
        // TODO WTF ?? we need to rewrite that app :/
        throw new UnsupportedOperationException();
    };

    long calculate(long level, long demand, long produced);
}
