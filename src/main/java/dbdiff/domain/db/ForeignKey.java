package dbdiff.domain.db;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ForeignKey extends Constraint {

    public ForeignKey(String name) {
        super(name);
    }
}
