package dbdiff.domain.db;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Getter
public class PrimaryKey extends Constraint {
    public PrimaryKey(String name) {
        super(name);
    }
}
