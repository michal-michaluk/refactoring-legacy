package acl;

import dao.ProductionDao;
import entities.ProductionEntity;
import shortages.ProductionOutputs;
import shortages.ProductionOutputsRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ProductionShortagesMediator implements ProductionOutputsRepository {

    private final ProductionDao productionDao;

    public ProductionShortagesMediator(ProductionDao productionDao) {
        this.productionDao = productionDao;
    }

    @Override
    public ProductionOutputs get(String productRefNo, LocalDate today) {
        List<ProductionEntity> productions = productionDao.findFromTime(productRefNo, today.atStartOfDay());
        return new ProductionOutputs(
                productions.stream()
                        .collect(Collectors.groupingBy(
                                production -> production.getStart().toLocalDate(),
                                Collectors.summingLong(ProductionEntity::getOutput)
                        ))
        );
    }
}
