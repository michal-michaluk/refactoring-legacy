package shortages;

import entities.ShortageEntity;

import java.util.List;

public class ShortagePredictionService {

    private ShortagerPredictionFactory factory;

    public ShortagePredictionService(ShortagerPredictionFactory factory) {
        this.factory = factory;
    }

    public List<ShortageEntity> predictShortagres() {
        ShortagePrediction shortagePrediction = factory.create();
        return shortagePrediction.predict();
    }
}
