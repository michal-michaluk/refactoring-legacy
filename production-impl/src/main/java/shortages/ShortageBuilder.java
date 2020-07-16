package shortages;

import entities.ShortageEntity;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class ShortageBuilder {

    private final String productRefNo;
    private final List<ShortageEntity> gap = new LinkedList<>();

    public static ShortageBuilder builder(String productRefNo) {
        return new ShortageBuilder(productRefNo);
    }

    private ShortageBuilder(String productRefNo) {
        this.productRefNo = productRefNo;
    }

    public void add(LocalDate day, long levelOnDelivery) {
        ShortageEntity entity = new ShortageEntity();
        entity.setRefNo(productRefNo);
        entity.setFound(LocalDate.now());
        entity.setAtDay(day);
        entity.setMissing(Math.abs(levelOnDelivery));
        gap.add(entity);
    }

    public List<ShortageEntity> build() {
        return gap;
    }
}
