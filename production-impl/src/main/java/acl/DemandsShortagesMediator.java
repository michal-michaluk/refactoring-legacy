package acl;

import demands.DemandsReadsService;
import demands.DemandsReadsService.Demand;
import shortages.Demands;
import shortages.DemandsRepository;
import shortages.LevelOnDeliveryVariantDecision;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DemandsShortagesMediator implements DemandsRepository {
    private final DemandsReadsService demands;

    public DemandsShortagesMediator(DemandsReadsService demands) {
        this.demands = demands;
    }

    @Override
    public Demands get(String productRefNo, LocalDate today) {
        List<Demand> entities = demands.findFrom(today.atStartOfDay(), productRefNo);

        return new Demands(entities.stream()
                .collect(Collectors.toUnmodifiableMap(
                        Demand::getDate,
                        entity -> new Demands.DailyDemand(
                                entity.getDemand(),
                                LevelOnDeliveryVariantDecision.pickCalculationVariant(entity.getSchema())))
                ));
    }
}
