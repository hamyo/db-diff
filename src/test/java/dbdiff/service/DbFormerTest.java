package dbdiff.service;

import dbdiff.domain.db.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DbFormerTest {
    private DbFormer former;

    @BeforeEach
    void init() {
        former = new DbFormer();
    }

    @Test
    void form() {
        List<DatabaseObject> dbObjs = new ArrayList<>();
        // Table
        dbObjs.add(Table.ofDesc("Ребенок", "CLD"));
        dbObjs.add(Table.ofName("MSK", "CHILD"));
        //Columns
        dbObjs.add(Column.ofDesc("PER_ID"));
        dbObjs.add(Column.ofName("PER_ID", "BIGINT"));
        dbObjs.add(Column.ofDesc("СНИЛС"));
        dbObjs.add(Column.ofName("CLD_SNILS", "BIGINT"));
        dbObjs.add(Column.ofDesc("Фамилия"));
        dbObjs.add(Column.ofDesc("при рождении"));
        dbObjs.add(Column.ofName("CLD_LASTNAME_ON_BIRTH", "VARCHAR(100)"));
        dbObjs.add(Column.ofName("APP_ID", "BIGINT"));
        // PK
        dbObjs.add(new PrimaryKey("P_CLD"));
        // Indices
        dbObjs.add(new Index("MSK", "I_CLD_APP_ID"));
        // FK
        dbObjs.add(new ForeignKey("F_CLD_PER"));
        dbObjs.add(new ForeignKey("F_CLD_APP"));

        // Table
        dbObjs.add(Table.ofName("MSK", "CLS_NOTIFICATION_TYPE"));
        //Columns
        dbObjs.add(Column.ofDesc("CNT_ID"));
        dbObjs.add(Column.ofName("CNT_ID", "INTEGER"));
        dbObjs.add(Column.ofDesc("Название"));
        dbObjs.add(Column.ofName("CNT_NAME", "VARCHAR(500)"));
        // PK
        dbObjs.add(new PrimaryKey("P_CNT"));
        // Indices
        dbObjs.add(new Index("MSK", "I_CNT_ID"));
        // FK
        dbObjs.add(new ForeignKey("F_CNT"));

        Database actual = former.form(dbObjs);
        assertAll("db",
                () -> assertThat(actual.getTables().size(), is(2)),
                () -> assertThat(actual.getTables().get(0).getColumns().size(), is(4)),
                () -> assertNotNull(actual.getTables().get(0).getPrimaryKey()),
                () -> assertThat(actual.getTables().get(0).getIndices().size(), is(1)),
                () -> assertThat(actual.getTables().get(0).getForeignKeys().size(), is(2)),
                () -> assertThat(actual.getTables().get(1).getColumns().size(), is(2)),
                () -> assertNotNull(actual.getTables().get(1).getPrimaryKey()),
                () -> assertThat(actual.getTables().get(1).getIndices().size(), is(1)),
                () -> assertThat(actual.getTables().get(1).getForeignKeys().size(), is(1))
        );
    }
}