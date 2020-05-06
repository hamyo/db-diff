package dbdiff.report;

import dbdiff.domain.db.*;
import dbdiff.domain.diff.Difference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class WordReportTest {
    private WordReport report;

    @BeforeEach
    void init() {
        report = new WordReport("report.docx");
    }

    @Test
    @Disabled
    void createAndSave() {
        List<Difference> diff = new ArrayList<>();
        Table newTable = Table.ofDesc("Типы прилагаемых документов", "CADT");
        newTable.merge(Table.ofName("MSK", "CLS_ATT_DOC_TYPE"));
        newTable.setPrimaryKey(new PrimaryKey("P_CADT"));
        Column col = Column.ofName("CADT_ID", "INTEGER");
        col.merge(Column.ofDesc("CADT_ID"));
        newTable.getColumns().add(col);

        col = Column.ofName("CADT_NAME", "VARCHAR(500)");
        col.merge(Column.ofDesc("Наименование"));
        newTable.getColumns().add(col);

        col = Column.ofName("CADT_SHORT_NAME", "VARCHAR(250)");
        col.merge(Column.ofDesc("Наименование сокращенное"));
        newTable.getColumns().add(col);

        col = Column.ofName("CADT_IS_IDENTITY", "SMALLINT");
        col.merge(Column.ofDesc("Признак удостоверения личности"));
        newTable.getColumns().add(col);

        newTable.getIndices().add(new Index("MSK", "I_CADT_NAME"));
        newTable.getIndices().add(new Index("MSK", "I_CADT_SHORT_NAME"));

        newTable.getForeignKeys().add(new ForeignKey("F_CSFE_ID"));
        diff.add(Difference.ofNew(newTable));

        Table editTable = Table.ofDesc("Направление расходования средств", "EXPD");
        editTable.merge(Table.ofName("MSK", "CLS_EXPENSE_DIRECTION"));

        col = Column.ofName("EXPD_ID", "INTEGER");
        col.merge(Column.ofDesc("ID"));
        editTable.getColumns().add(col);

        col = Column.ofName("EXPD_NAME", "VARCHAR(500)");
        col.merge(Column.ofDesc("Наименование направления расходования"));
        editTable.getColumns().add(col);
        diff.add(Difference.ofNew(newTable));

        report.create(diff);
    }
}