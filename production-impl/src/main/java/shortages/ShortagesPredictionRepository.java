package shortages;

import java.time.LocalDate;

public interface ShortagesPredictionRepository {
    ShortagePrediction create(String productRefNo, LocalDate today, int daysAhead);
}
