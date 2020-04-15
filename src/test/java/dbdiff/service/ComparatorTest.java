package dbdiff.service;

import dbdiff.domain.conf.Config;
import dbdiff.domain.db.*;
import dbdiff.domain.diff.Difference;
import dbdiff.domain.diff.StateChange;
import dbdiff.parser.DbZos;
import dbdiff.report.WordReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

class ComparatorTest {
    private Comparator comparator;

    @BeforeEach
    void init() {
        comparator = new Comparator(new DbZos(), new DbFormer(), new WordReport(""), new Config());
    }

    @Test
    void compare_NewTable() {
        Database old = new Database();
        old.getTables().add(Table.ofName("MSK", "CHILD"));

        Database current = new Database();
        Table table = Table.ofName("MSK", "CLS_ESTABLISHMENTS");
        table.getColumns().add(Column.ofName("ID", "BIGINT"));
        current.getTables().add(table);

        List<Difference> diff = comparator.compare(old, current);
        assertAll("compare",
                () -> assertThat(diff.size(), is(1)),
                () -> assertThat(diff.get(0).getState(), is(StateChange.CREATION)),
                () -> assertThat(diff.get(0).getTable().getColumns().size(), is(1))
        );
    }

    @Test
    void compare_NewColumn() {
        Database old = new Database();
        Table table = Table.ofName("MSK", "CHILD");
        table.getColumns().add(Column.ofName("ID", "BIGINT"));
        old.getTables().add(table);

        Database current = new Database();
        table = Table.ofName("MSK", "CHILD");
        table.getColumns().add(Column.ofName("ID", "BIGINT"));
        table.getColumns().add(Column.ofName("NAME", "VARCHAR(100)"));
        table.getColumns().add(Column.ofName("GENDER", "SMALLINT"));
        current.getTables().add(table);

        List<Difference> diff = comparator.compare(old, current);
        assertAll("compare",
                () -> assertThat(diff.size(), is(1)),
                () -> assertThat(diff.get(0).getState(), is(StateChange.EDITING)),
                () -> assertThat(diff.get(0).getTable().getColumns().size(), is(2))
        );
    }

    @Test
    void compare_NewIndex() {
        Database old = new Database();
        Table table = Table.ofName("MSK", "CHILD");
        table.getColumns().add(Column.ofName("ID", "BIGINT"));
        old.getTables().add(table);

        Database current = new Database();
        table = Table.ofName("MSK", "CHILD");
        table.getColumns().add(Column.ofName("ID", "BIGINT"));
        table.getIndices().add(new Index("MSK", "I_ID"));
        current.getTables().add(table);

        List<Difference> diff = comparator.compare(old, current);
        assertAll("compare",
                () -> assertThat(diff.size(), is(1)),
                () -> assertThat(diff.get(0).getState(), is(StateChange.EDITING)),
                () -> assertThat(diff.get(0).getTable().getColumns().size(), is(0)),
                () -> assertThat(diff.get(0).getTable().getIndices().size(), is(1))
        );
    }

    @Test
    void compare_NewFK() {
        Database old = new Database();
        Table table = Table.ofName("MSK", "CHILD");
        table.getColumns().add(Column.ofName("ID", "BIGINT"));
        old.getTables().add(table);

        Database current = new Database();
        table = Table.ofName("MSK", "CHILD");
        table.getColumns().add(Column.ofName("ID", "BIGINT"));
        table.getForeignKeys().add(new ForeignKey("FK_ID"));
        current.getTables().add(table);

        List<Difference> diff = comparator.compare(old, current);
        assertAll("compare",
                () -> assertThat(diff.size(), is(1)),
                () -> assertThat(diff.get(0).getState(), is(StateChange.EDITING)),
                () -> assertThat(diff.get(0).getTable().getColumns().size(), is(0)),
                () -> assertThat(diff.get(0).getTable().getForeignKeys().size(), is(1))
        );
    }

    @Test
    void compare_NoChange() {
        Database old = new Database();
        Table table = Table.ofName("MSK", "CHILD");
        table.getColumns().add(Column.ofName("ID", "BIGINT"));
        table.getForeignKeys().add(new ForeignKey("FK_ID"));
        table.getIndices().add(new Index("MSK", "I_ID"));
        old.getTables().add(table);

        Database current = new Database();
        table = Table.ofName("MSK", "CHILD");
        table.getColumns().add(Column.ofName("ID", "BIGINT"));
        table.getForeignKeys().add(new ForeignKey("FK_ID"));
        table.getIndices().add(new Index("MSK", "I_ID"));
        current.getTables().add(table);

        List<Difference> diff = comparator.compare(old, current);
        assertAll("compare",
                () -> assertThat(diff.size(), is(0))
        );
    }
}