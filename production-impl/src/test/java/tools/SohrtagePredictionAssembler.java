package tools;

import entities.DemandEntity;
import entities.ProductionEntity;
import external.CurrentStock;
import shortages.Demands;
import shortages.LevelOnDelivery;
import shortages.ProductionOutputs;
import shortages.ShortagePrediction;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class SohrtagePredictionAssembler {

    private CurrentStock stock;
    private ProductionOutputs outputs;
    private Demands demands;

    public SohrtagePredictionAssembler stock(CurrentStock stock) {
        this.stock = stock;
        return this;
    }

    public SohrtagePredictionAssembler outputs(ProductionOutputs outputs) {
        this.outputs = outputs;
        return this;
    }

    public SohrtagePredictionAssembler demands(Demands demands) {
        this.demands = demands;
        return this;
    }

    public SohrtagePredictionAssembler outputs(List<ProductionEntity> productions) {
        this.outputs = new ProductionOutputs(productions.stream().collect(Collectors.groupingBy(
                production -> production.getStart().toLocalDate(),
                Collectors.summingLong(ProductionEntity::getOutput)
        )));
        return this;
    }

    public SohrtagePredictionAssembler demands(List<DemandEntity> demands) {
        this.demands = new Demands(demands.stream().collect(Collectors.toMap(
                DemandEntity::getDay,
                entity -> new Demands.DailyDemand(
                        Util.getLevel(entity),
                        LevelOnDelivery.pick(Util.getDeliverySchema(entity))
                )
        )));
        return this;
    }

    public ShortagePrediction build(LocalDate startDate) {
        List<LocalDate> dates = Stream.iterate(startDate.plusDays(1), date -> date.plusDays(1))
                .limit(7)
                .collect(toList());
        return new ShortagePrediction("300900", stock, dates, outputs, demands);
    }
}
