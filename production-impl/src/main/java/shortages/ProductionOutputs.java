package shortages;

import entities.ProductionEntity;

import java.time.LocalDate;
import java.util.*;

public class ProductionOutputs {

    private final Map<LocalDate, List<ProductionEntity>> outputs;
    private final String productRefNo;

    public ProductionOutputs(List<ProductionEntity> productions) {
        outputs = new HashMap<>();
        String productRefNo = null;
        for (ProductionEntity production : productions) {
            if (!outputs.containsKey(production.getStart().toLocalDate())) {
                outputs.put(production.getStart().toLocalDate(), new ArrayList<>());
            }
            outputs.get(production.getStart().toLocalDate()).add(production);
            productRefNo = production.getForm().getRefNo();
        }
        this.productRefNo = productRefNo;
    }

    public long get(LocalDate day) {
        long level = 0;
        for (ProductionEntity production : outputs.getOrDefault(day, Collections.emptyList())) {
            level += production.getOutput();
        }
        return level;
    }

    public String getProductRefNo() {
        return productRefNo;
    }
}
