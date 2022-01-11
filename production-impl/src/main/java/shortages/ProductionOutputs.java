package shortages;

import entities.ProductionEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductionOutputs {
    private final String productRefNo;
    private final Map<LocalDate, List<ProductionEntity>> outputs;

    public ProductionOutputs(List<ProductionEntity> productions) {
        this.productRefNo = productions.get(productions.size() - 1).getForm().getRefNo();
        this.outputs = productions.stream()
                .collect(Collectors.groupingBy(
                        production -> production.getStart().toLocalDate()
                ));
    }

    public long getLevel(LocalDate day) {
        return outputs.getOrDefault(day, List.of()).stream()
                .mapToLong(ProductionEntity::getOutput)
                .sum();
    }

    public String getProductRefNo() {
        return productRefNo;
    }
}
