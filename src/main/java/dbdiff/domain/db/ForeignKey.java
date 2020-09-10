package dbdiff.domain.db;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
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
