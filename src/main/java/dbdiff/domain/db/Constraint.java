package dbdiff.domain.db;

import lombok.Getter;

@Getter
public class Constraint implements DatabaseObject {
    String name;

    protected Constraint(String name) {
        this.name = name;
    }
}
