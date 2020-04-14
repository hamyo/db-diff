package dbdiff.domain.db;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ForeignKey extends Constraint {

    public ForeignKey(String name) {
        super(name);
    }
}
