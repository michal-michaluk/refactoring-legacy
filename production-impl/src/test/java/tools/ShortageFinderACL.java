package tools;

import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import shortages.ShortagePrediction;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ShortageFinderACL {

    private static boolean calculateWithNewVersion = true;

    private ShortageFinderACL() {
    }

    /**
     * Production at day of expected delivery is quite complex:
     * We are able to produce and deliver just in time at same day
     * but depending on delivery time or scheme of multiple deliveries,
     * we need to plan properly to have right amount of parts ready before delivery time.
     * <p/>
     * Typical schemas are:
     * <li>Delivery at prod day start</li>
     * <li>Delivery till prod day end</li>
     * <li>Delivery during specified shift</li>
     * <li>Multiple deliveries at specified times</li>
     * Schema changes the way how we calculate shortages.
     * Pick of schema depends on customer demand on daily basis and for each product differently.
     * Some customers includes that information in callof document,
     * other stick to single schema per product. By manual adjustments of demand,
     * customer always specifies desired delivery schema
     * (increase amount in scheduled transport or organize extra transport at given time)
     */
    public static List<ShortageEntity> findShortages(LocalDate today, int daysAhead, CurrentStock stock,
                                                     List<ProductionEntity> productions, List<DemandEntity> demands) {
//            ShortagePredictionFactory factory = new ShortagePredictionFactory(stock, productions, demands);
//            ShortagePrediction prediction = factory.create(productRefNo, today, daysAhead);
//        return prediction.predict().build();
        return null;
    }

    private static void log(ShortagePrediction prediction, List<ShortageEntity> oldCalculation, Map<LocalDate, Long> diff) {
        // log as json prediction and any way oldCalculation, diff
        System.out.println((diff.isEmpty() ? "OK" : "NOK") + ", " + json(prediction) + ", " + oldCalculation + ", " + diff);
    }

    private static String json(ShortagePrediction prediction) {
        return "null";
    }

    private static Map<LocalDate, Long> diff(List<ShortageEntity> oldCalculation, List<ShortageEntity> newCalcualtion) {
        Map<LocalDate, Long> oldMap = oldCalculation.stream().collect(Collectors.toMap(
                ShortageEntity::getAtDay, ShortageEntity::getMissing
        ));
        Map<LocalDate, Long> newMap = newCalcualtion.stream().collect(Collectors.toMap(
                ShortageEntity::getAtDay, ShortageEntity::getMissing
        ));
        if (!oldMap.equals(newMap)) {
            return oldMap.entrySet().stream().map(e -> Map.entry(
                            e.getKey(),
                            e.getValue() - newMap.get(e.getKey())
                    )).filter(e -> Objects.equals(e.getValue(), 0L))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey, Map.Entry::getValue
                    ));
        } else {
            return Map.of();
        }
    }

}
