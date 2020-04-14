package dbdiff.domain.diff;

import dbdiff.domain.db.Table;
import lombok.Getter;

@Getter
public class Difference {
    StateChange state;
    Table table;

    private Difference(StateChange state, Table table) {
        this.state = state;
        this.table = table;
    }

    public static Difference ofNew(Table table) {
        return new Difference(StateChange.CREATION, table);
    }

    public static Difference ofEdit(Table table) {
        return new Difference(StateChange.EDITING, table);
    }
}
