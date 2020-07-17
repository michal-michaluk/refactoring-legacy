package shortages;

import java.time.LocalDate;
import java.util.Map;

public class ProductionOutputs {

    private final Map<Object, Long> outputs;

    public ProductionOutputs(Map<Object, Long> outputs) {
        this.outputs = outputs;
    }

    public long get(LocalDate day) {
        return outputs.getOrDefault(day, 0L);
    }
}
