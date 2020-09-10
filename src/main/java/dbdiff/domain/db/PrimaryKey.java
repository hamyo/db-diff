package dbdiff.domain.db;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Getter
@ToString(callSuper = true)
public class PrimaryKey extends Constraint {
    public PrimaryKey(String name) {
        super(name);
    }

    @Override
    public String getTypeName() {
        return "PRIMARY KEY";
    }
}
