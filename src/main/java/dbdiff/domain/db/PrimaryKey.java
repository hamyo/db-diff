package dbdiff.domain.db;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PrimaryKey extends Constraint {
    public PrimaryKey(String name) {
        super(name);
    }
}
