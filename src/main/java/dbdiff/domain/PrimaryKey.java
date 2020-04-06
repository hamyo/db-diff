package dbdiff.domain;

import lombok.Data;

@Data
public class PrimaryKey extends Constraint {
    public PrimaryKey(String name) {
        super(name);
    }
}
