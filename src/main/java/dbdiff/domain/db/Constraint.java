package dbdiff.domain.db;

import lombok.Data;

@Data
public class Constraint implements DatabaseObject {
    String name;

    protected Constraint(String name) {
        this.name = name;
    }
}
