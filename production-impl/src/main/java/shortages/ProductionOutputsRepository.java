package shortages;

import java.time.LocalDate;

public interface ProductionOutputsRepository {
    ProductionOutputs get(String productRefNo, LocalDate today);
}
