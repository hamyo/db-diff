package dbdiff.domain.db;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Constraint implements DatabaseObject {
    String name;

    protected Constraint(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return "CONSTRAINT";
    }
}
