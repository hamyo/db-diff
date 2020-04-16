package dbdiff.domain.db;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ForeignKey extends Constraint {
    String type = "CONSTRAINT";

    public ForeignKey(String name) {
        super(name);
    }

    @Override
    public String getTypeName() {
        return "FOREIGN KEY";
    }
}
