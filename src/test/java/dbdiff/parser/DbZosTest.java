package dbdiff.parser;

import dbdiff.domain.db.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class DbZosTest {

    static Stream<Arguments> columnLinesProvider() {
        return Stream.of(
                arguments("CAF_CODE VARCHAR(500) NOT NULL    ,", "CAF_CODE", "VARCHAR(500)"),
                arguments("ML_ID BIGINT NOT NULL   GENERATED ALWAYS AS IDENTITY ,", "ML_ID", "BIGINT"),
                arguments("PAY_SUM DECIMAL(10, 2) NOT NULL    ,", "PAY_SUM", "DECIMAL(10, 2)"),
                arguments("PF_CREATION_DATE   TIMESTAMP,", "PF_CREATION_DATE", "TIMESTAMP"),
                arguments("PAY_SUM DECIMAL(10, 2),", "PAY_SUM", "DECIMAL(10, 2)")
        );
    }

    @ParameterizedTest
    @DisplayName("Test for column parsing")
    @MethodSource("columnLinesProvider")
    void handleOne_Column(String line, String name, String type) {
        DbZos parser = new DbZos();
        Optional<DatabaseObject> actual = parser.handleOne(line);
        assertAll("column",
                () -> assertTrue(actual.isPresent()),
                () -> assertTrue(actual.get() instanceof Column),
                () -> assertEquals(((Column) actual.get()).getName(), name),
                () -> assertEquals(((Column) actual.get()).getType(), type));
    }


    static Stream<Arguments> tableLinesProvider() {
        return Stream.of(
                arguments("CREATE TABLE MSK.SPU_EXCHANGE_REGISTRY (", "MSK", "SPU_EXCHANGE_REGISTRY"),
                arguments("CREATE TABLE MSK.ANNEX_52A (", "MSK", "ANNEX_52A"),
                arguments("CREATE TABLE MSK.PERSON (", "MSK", "PERSON")
        );
    }

    @ParameterizedTest
    @DisplayName("Test for table's parsing")
    @MethodSource("tableLinesProvider")
    void handleOne_Table(String line, String scheme, String name) {
        DbZos parser = new DbZos();
        Optional<DatabaseObject> actual = parser.handleOne(line);
        assertAll("table",
                () -> assertTrue(actual.isPresent()),
                () -> assertThat(actual.get(), instanceOf(Table.class)),
                () -> assertEquals(((Table) actual.get()).getScheme(), scheme),
                () -> assertEquals(((Table) actual.get()).getName(), name));
    }

    static Stream<Arguments> tableDescLinesProvider() {
        return Stream.of(
                arguments("-- Персона:PER", "Персона", "PER"),
                arguments("-- Лог по решениям для КИП : KDL", "Лог по решениям для КИП", "KDL"),
                arguments("-- Приложение 52а:AXA", "Приложение 52а", "AXA")
        );
    }

    @ParameterizedTest
    @DisplayName("Test for table description's parsing")
    @MethodSource("tableDescLinesProvider")
    void handleOne_TableDesc(String line, String desc, String tablespace) {
        DbZos parser = new DbZos();
        Optional<DatabaseObject> actual = parser.handleOne(line);
        assertAll("table",
                () -> assertTrue(actual.isPresent()),
                () -> assertThat(actual.get(), instanceOf(Table.class)),
                () -> assertEquals(((Table) actual.get()).getDesc(), desc),
                () -> assertEquals(((Table) actual.get()).getTablespace(), tablespace));
    }

    static Stream<Arguments> indexLinesProvider() {
        return Stream.of(
                arguments("CREATE  UNIQUE INDEX MSK.I_VALIDATION ON MSK.VALIDATION (DOC_ID, VT_ID);", "MSK", "I_VALIDATION"),
                arguments("CREATE INDEX  MSK.I_VL_VT ON MSK.VALIDATION (VT_ID);", "MSK", "I_VL_VT")
        );
    }

    @ParameterizedTest
    @DisplayName("Test for index's parsing")
    @MethodSource("indexLinesProvider")
    void handleOne_Index(String line, String scheme, String name) {
        DbZos parser = new DbZos();
        Optional<DatabaseObject> actual = parser.handleOne(line);
        assertAll("index",
                () -> assertTrue(actual.isPresent()),
                () -> assertThat(actual.get(), instanceOf(Index.class)),
                () -> assertEquals(((Index) actual.get()).getScheme(), scheme),
                () -> assertEquals(((Index) actual.get()).getName(), name));
    }

    static Stream<Arguments> primaryKeyLinesProvider() {
        return Stream.of(
                arguments("CONSTRAINT P_CEST PRIMARY KEY (CEST_ID)", "P_CEST"),
                arguments("CONSTRAINT  P_NC  PRIMARY KEY   (NC_DIC_NUM,NC_REC_NUM,NC_NAME)", "P_NC")
        );
    }

    @ParameterizedTest
    @DisplayName("Test for primary key's parsing")
    @MethodSource("primaryKeyLinesProvider")
    void handleOne_ParimaryKey(String line, String name) {
        DbZos parser = new DbZos();
        Optional<DatabaseObject> actual = parser.handleOne(line);
        assertAll("pk",
                () -> assertTrue(actual.isPresent()),
                () -> assertThat(actual.get(), instanceOf(PrimaryKey.class)),
                () -> assertEquals(((PrimaryKey) actual.get()).getName(), name));
    }

    static Stream<Arguments> foreignKeyLinesProvider() {
        return Stream.of(
                arguments("ALTER TABLE MSK.CITIZEN_INFO ADD CONSTRAINT F_CINF_ADDR_STAY FOREIGN KEY (ADDR_STAY_ID) REFERENCES MSK.ADDRESS (ADDR_ID) ON DELETE RESTRICT;", "F_CINF_ADDR_STAY"),
                arguments("ALTER   TABLE  MSK.APP_RESPONSE_VALUES  ADD   CONSTRAINT   F_AS_ARV  FOREIGN   KEY  (AS_ID)  REFERENCES MSK.APP_RESPONSE (AS_ID) ON DELETE RESTRICT;", "F_AS_ARV")
        );
    }

    @ParameterizedTest
    @DisplayName("Test for foreign key's parsing")
    @MethodSource("foreignKeyLinesProvider")
    void handleOne_ForeignKey(String line, String name) {
        DbZos parser = new DbZos();
        Optional<DatabaseObject> actual = parser.handleOne(line);
        assertAll("fk",
                () -> assertTrue(actual.isPresent()),
                () -> assertThat(actual.get(), instanceOf(ForeignKey.class)),
                () -> assertEquals(((ForeignKey) actual.get()).getName(), name));
    }

    static Stream<Arguments> columnDescLinesProvider() {
        return Stream.of(
                arguments("-- ID ответа из ведомства", "ID ответа из ведомства"),
                arguments("-- Название основания", "Название основания"),
                arguments("-- ID", "ID")
        );
    }

    @ParameterizedTest
    @DisplayName("Test for column description's parsing")
    @MethodSource("columnDescLinesProvider")
    void handleOne_ColumnDesc(String line, String desc) {
        DbZos parser = new DbZos();
        Optional<DatabaseObject> actual = parser.handleOne(line);
        assertAll("columnDesc",
                () -> assertTrue(actual.isPresent()),
                () -> assertThat(actual.get(), instanceOf(Column.class)),
                () -> assertEquals(((Column) actual.get()).getDesc(), desc));
    }
}