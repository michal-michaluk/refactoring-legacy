package shortages;

import entities.ShortageEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ShortageBuilder {

    private final String productRefNo;
    private final List<ShortageEntity> shortages = new LinkedList<>();

    public ShortageBuilder(String productRefNo) {
        this.productRefNo = productRefNo;
    }

    public static ShortageBuilder builder(String productRefNo) {
        return new ShortageBuilder(productRefNo);
    }

    public void foundForDay(LocalDate day, long levelOnDelivery) {
        ShortageEntity entity = new ShortageEntity();
        entity.setRefNo(productRefNo);
        entity.setFound(LocalDate.now());
        entity.setAtDay(day);
        entity.setMissing(Math.abs(levelOnDelivery));
        shortages.add(entity);
    }

    public List<ShortageEntity> build() {
        return Collections.unmodifiableList(shortages);
    }
}
