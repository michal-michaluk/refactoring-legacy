package shortages;

import entities.FormEntity;
import entities.LineEntity;
import entities.ProductionEntity;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ExampleProductionsPlan {

    private static AtomicLong ids = new AtomicLong(0);
    private static LocalDate date = LocalDate.now();

    public static List<ProductionEntity> productions() {
        return Arrays.asList(
                prod(0, 1, 7), prod(0, 1, 14),
                prod(0, 2, 7), prod(0, 2, 14),
                prod(0, 3, 7), prod(0, 3, 14),
                prod(0, 4, 7), prod(0, 4, 14),
                prod(0, 5, 7), prod(0, 5, 14),
                prod(0, 6, 7), prod(0, 6, 14),
                prod(0, 7, 7), prod(0, 7, 14)
        );
    }


    private static ProductionEntity prod(long lineId, int plusDays, int hour) {
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

    private static LineEntity createLine(long id) {
        LineEntity line = new LineEntity();
        line.setId(id);
        line.setMaxWeight(10_000);
        return line;
    }

    private static FormEntity createForm300900() {
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
