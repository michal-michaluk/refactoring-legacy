package tools;

import acl.ShortageFinderACL;
import entities.FormEntity;
import entities.LineEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import external.CurrentStock;
import org.junit.Test;
import shortages.ExampleDemands;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static shortages.ShortagesAssert.assertThat;

public class ShortageFinderTest {

    AtomicLong ids = new AtomicLong(0);
    private LocalDate date = LocalDate.now();

    @Test
    public void findShortages() {
        CurrentStock stock = new CurrentStock(1000, 200);
        print(stock);
        List<ShortageEntity> shortages = ShortageFinderACL.findShortages(
                date.plusDays(1), 7,
                stock,
                productions(
                        prod(0, 1, 7), prod(0, 1, 14),
                        prod(0, 2, 7), prod(0, 2, 14),
                        prod(0, 3, 7), prod(0, 3, 14),
                        prod(0, 4, 7), prod(0, 4, 14),
                        prod(0, 5, 7), prod(0, 5, 14),
                        prod(0, 6, 7), prod(0, 6, 14),
                        prod(0, 7, 7), prod(0, 7, 14)
                ),
                ExampleDemands.demandSequence(date.plusDays(2), 17000, 17000)
        );

        assertThat(shortages)
                .foundExactly(2)
                .missingPartsAt(date.plusDays(2), 3400)
                .missingPartsAt(date.plusDays(3), 7800);
    }

    private void print(CurrentStock stock) {
        System.out.println("shortages: " + stock.getLevel());
    }

    private void print(List<ShortageEntity> shortages) {
        System.out.println("shortages: " + shortages.stream().map(s -> s.getAtDay() + " " + s.getMissing())
                .collect(Collectors.joining(", ")));
    }

    private List<ProductionEntity> productions(ProductionEntity... productions) {
        System.out.println("production: " + Stream.of(productions)
                .map(prod -> prod.getStart().toLocalDate() + " " + prod.getOutput())
                .collect(Collectors.joining(", ")));
        return asList(productions);
    }


    private ProductionEntity prod(long lineId, int plusDays, int hour) {
        ProductionEntity entity = new ProductionEntity();
        LineEntity line = createLine(lineId);
        FormEntity form = createForm300900();
        entity.setProductionId(ids.getAndIncrement());
        entity.setLine(line);
        entity.setForm(form);
        entity.setStart(date.plusDays(plusDays).atTime(hour, 0));
        entity.setDuration(Duration.ofHours(4));
        entity.setStartAndWormUp(Duration.ofMinutes(20));
        entity.setEndAndCleaning(Duration.ofMinutes(10));
        entity.setSpeed(1.0);
        entity.setOutput(
                (long) (entity.getSpeed() *
                        entity.getDuration()
                                .minus(entity.getStartAndWormUp())
                                .minus(entity.getEndAndCleaning()).getSeconds()
                        / 60 * form.getOutputPerMinute())
        );
        entity.setUtilization(2.0);
        entity.setColor(null);
        entity.setNote(null);
        return entity;
    }

    private LineEntity createLine(long id) {
        LineEntity line = new LineEntity();
        line.setId(id);
        line.setMaxWeight(10_000);
        return line;
    }

    private FormEntity createForm300900() {
        FormEntity form = new FormEntity();
        form.setRefNo("300900");
        form.setOutputPerMinute(30);
        form.setUtilization(2.0);
        form.setWeight(5_000);
        form.setStartAndWormUp(Duration.ofMinutes(20));
        form.setEndAndCleaning(Duration.ofMinutes(10));
        return form;
    }
}
