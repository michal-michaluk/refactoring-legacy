package tools;

import acl.ShortageFinderACL;
import entities.DemandEntity;
import entities.OriginalDemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import enums.DeliverySchema;
import external.CurrentStock;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class ShortageFinderSimpleTest {

    AtomicLong ids = new AtomicLong(0);
    private LocalDate date = LocalDate.now();

    @Test
    public void calculateWithoutProductionsOnPlan() {
        // given
        CurrentStock stock = new CurrentStock(1000, 0);
        List<ProductionEntity> noProductions = withoutProductionsOnPlan();
        List<DemandEntity> demands = demands(demand(2, 1000));

        // when
        List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                date.plusDays(1), 7,
                stock,
                noProductions,
                demands
        );

        // then
        Assert.assertTrue(shortages.isEmpty());
    }

    private List<ProductionEntity> withoutProductionsOnPlan() {
        return Collections.emptyList();
    }

    private List<ProductionEntity> productions(ProductionEntity... productions) {
        System.out.println("production: " + Stream.of(productions)
                .map(prod -> prod.getStart().toLocalDate() + " " + prod.getOutput())
                .collect(Collectors.joining(", ")));
        return asList(productions);
    }

    private List<DemandEntity> demands(DemandEntity... demands) {
        System.out.println("demands: " + Stream.of(demands)
                .map(prod -> prod.getDay() + " " + prod.getOriginal().getLevel())
                .collect(Collectors.joining(", ")));
        return asList(demands);
    }

    private DemandEntity demand(int plusDays, int level) {
        DemandEntity entity = new DemandEntity();
        entity.setId(ids.getAndIncrement());
        entity.setCallofDate(date.minusDays(2));
        entity.setProductRefNo("300900");
        entity.setAtDay(date.plusDays(plusDays));
        OriginalDemandEntity original = new OriginalDemandEntity();
        original.setAtDay(date.plusDays(plusDays));
        original.setLevel(level);
        original.setDeliverySchema(DeliverySchema.atDayStart);
        entity.setOriginal(original);
        entity.setAdjustment(new ArrayList<>());
        return entity;
    }
}
