package shortages;

import entities.ShortageEntity;
import org.assertj.core.api.Assertions;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ShortagesAssert {
    private final List<ShortageEntity> shortages;

    public ShortagesAssert(List<ShortageEntity> shortages) {
        this.shortages = shortages;
    }

    public static ShortagesAssert assertThat(List<ShortageEntity> shortages) {
        return new ShortagesAssert(shortages);
    }

    public ShortagesAssert foundExactly(int foundEntries) {
        Assertions.assertThat(shortages).hasSize(foundEntries);
        return this;
    }

    public ShortagesAssert noShortagesFound() {
        foundExactly(0);
        return this;
    }

    public ShortagesAssert missingPartsAt(LocalDate date, long missing) {
        Optional<ShortageEntity> entry = shortages.stream().filter(e -> e.getAtDay().equals(date)).findFirst();
        Assertions.assertThat(entry).isPresent();
        Assertions.assertThat(entry.map(ShortageEntity::getMissing)).hasValue(missing);
        return this;
    }
}
